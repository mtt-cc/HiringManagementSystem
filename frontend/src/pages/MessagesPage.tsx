// frontend/src/pages/MessagesPage.tsx

import React, { useContext, useEffect, useState } from "react";
import { MessageType, Channel } from "../types/Message.ts";
import { MessageList } from "../components/MessageList.tsx";
import {
    Button,
    Card,
    Col,
    Container,
    Form,
    InputGroup,
    Modal,
    Offcanvas,
    Row,
    Spinner,
    ToggleButton
} from "react-bootstrap";
import NewMessageForm from "../components/NewMessageForm.tsx";
import AddIcon from '@mui/icons-material/Add'; // Import the Material-UI icon
import {getMessages, postMessage, updateMessageState} from "../api/crm.ts";
import ErrorModalRFC7807, { ErrorRFC7807 } from "../components/ErrorModalRFC7807.tsx";
import { AuthContext, NavigationContext } from "../Context.tsx";
import { Pageable } from "../types/Pageable.ts";
import PageBar from "../components/PageBar.tsx"; // Import the CSS file

const MessagesPage: React.FC = () => {
    const navigation = useContext(NavigationContext);
    const { user } = useContext(AuthContext);
    const [error, setError] = useState<ErrorRFC7807 | null>(null);
    // states for pagination
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [isLoading, setIsLoading] = useState(true);
    const [showFilters, setShowFilter] = useState(false);
    type MessageStateFilter = "not-discarded" | "received" | "read" | "discarded" | "done";
    const [categoryFilter, setCategoryFilter] = useState<MessageStateFilter>("not-discarded");
    const [sorting, setSorting] = useState<"ascending" | "descending">("descending");
    const [reload, setReload] = useState(false);
    // list of pageable of messages fetched
    const [messages, setMessages] = useState<Pageable<MessageType>>();
    const [show, setShow] = useState(false);

    // Initial values for the form
    const initialValues = {
        id: 1,
        subject: '',
        sender: '',
        channel: Channel.PhoneNumber,
        body: '',
        timestamp: '',
        actual_message_state: ''
    } as MessageType;

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    // Redirect to home if not logged in
    useEffect(() => {
        if (user === undefined || !user.status) {
            navigation.routes.home.to();
        }
    }, [user]);
    // to fetch messages
    useEffect(() => {
        setIsLoading(true);
        getMessages(
            page,
            size,
            sorting,
            categoryFilter,
            (data: Pageable<MessageType>) => setMessages(data),
            (err: ErrorRFC7807) => setError(err),
            () => setIsLoading(false)
        )
    }, [page, size, reload]);

    // change local state to hide message, and in the meantime change the
    //  state in the backend
    const discardMessage = (id: number) => {
        setMessages(prevMessages => ({
            ...prevMessages,
            content: prevMessages?.content.filter(message => message.id !== id) ?? []
        }));
        updateMessageState(
            id,
            "discarded",
            user?.xsrfToken!!,
            () =>{
                setReload(r => !r);
            },
            (error: ErrorRFC7807) => setError(error),
            () => {}
        );
    };

    const handleSubmit = async (values: MessageType, { setSubmitting }: any) => {
        postMessage(
            values,
            user?.xsrfToken!!,
            // add route to messages
            () => {
                handleClose();
                setReload(r => !r);
            },
            (error: ErrorRFC7807) => setError(error),
            () => setSubmitting(false)
        );
    };

    const handleMessageClick = (message: MessageType) => {

        if (message.actual_message_state.value !== "DISCARDED" && message.actual_message_state.value !== "READ") {
            // change local state and send request to backend
            message.actual_message_state.value = "READ";
            updateMessageState(
                message.id,
                "READ",
                user?.xsrfToken!!,
                () =>{},
                (error: ErrorRFC7807) => setError(error),
                () => {}
            );
        }
        // else navigate but dont change the state
        navigation.routes.message.to({ state: { message } });
    };

    if (isLoading) {
        return (
            <Container className="mt-3 d-flex justify-content-center">
                <Card className="mt-5" style={{ width: "20rem" }}>
                    <Card.Body className="d-flex justify-content-center">
                        <h4 className="me-2 mb-0">Loading...</h4> <Spinner variant="primary" animation="border" />
                    </Card.Body>
                </Card>
            </Container>
        );
    }

    return (
        <Container className="mt-3">
            {error !== null && <ErrorModalRFC7807 error={error} onHide={() => setError(null)} show={true} />}

            {/* Title and New Contact Button */}
            <Row>
                <Col className="col-2">
                    <InputGroup style={{ position: "relative", left: "1rem" }}>
                        <InputGroup.Text># Elements</InputGroup.Text>
                        <Form.Select
                            value={size}
                            onChange={(e) => {
                                setSize(parseInt(e.target.value))
                                setPage(0)
                            }}
                        >
                            <option value={10}>10</option>
                            <option value={20}>20</option>
                            <option value={50}>50</option>
                            <option value={100}>100</option>
                        </Form.Select>
                    </InputGroup>
                </Col>
                <Col className=" col-10 d-flex justify-content-end align-items-center mb-3">
                    <Button className="w-25 me-3" variant="secondary" onClick={() => setShowFilter(true)}>
                        Filters
                    </Button>
                    <Button variant="primary" className="floating-button" onClick={handleShow}>
                        <AddIcon className="button-icon" /> <span className="button-text">add new received message</span>
                    </Button>
                </Col>
            </Row>

            {/* The list of contacts */}
            {
                messages !== undefined ?
                    <>
                        <MessageList messages={messages.content} discardMessage={discardMessage} onMessageClick={handleMessageClick} />
                        <Row>
                            <Col className="d-flex justify-content-center">
                                {
                                    messages.totalPages > 1 ?
                                        <PageBar
                                            pageNumber={messages.pageable.pageNumber}
                                            totalPages={messages.totalPages}
                                            setPage={setPage}
                                        />
                                        :
                                        null
                                }
                            </Col>

                        </Row>
                    </>
                    :
                    null
            }
            {/*// Modal for adding a new message*/}
            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Add received message manually</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <NewMessageForm handleSubmit={handleSubmit} initialValues={initialValues} submitLabel={"Create message"} handleClose={handleClose} setReload={setReload} />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>

            {/*<MessageFilters />*/}
            <Offcanvas placement="end" style={{ width: "45rem" }} show={showFilters} onHide={() => setShowFilter(false)}>
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>Filters</Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>
                    <InputGroup>
                        <ToggleButton
                            id={"category-all"}
                            value={"not-discarded"}
                            variant="outline-primary"
                            type="radio"
                            name="category"
                            checked={categoryFilter === "not-discarded"}
                            onChange={() => setCategoryFilter("not-discarded")}
                            style={{ width: "25%", height: "3.5rem", borderRadius: "0.5rem 0 0 0.5rem" }}
                            className="d-flex justify-content-center align-items-center"
                        >All</ToggleButton>
                        <ToggleButton
                            id={"category-received"}
                            value={"received"}
                            variant="outline-primary"
                            type="radio"
                            name="category"
                            checked={categoryFilter === "received"}
                            onChange={() => setCategoryFilter("received")}
                            style={{ width: "25%" }}
                            className="d-flex justify-content-center align-items-center"
                        >Received</ToggleButton>
                        <ToggleButton
                            id={"category-read"}
                            value={"read"}
                            variant="outline-primary"
                            type="radio"
                            name="category"
                            checked={categoryFilter === "read"}
                            onChange={() => setCategoryFilter("read")}
                            style={{ width: "25%" }}
                            className="d-flex justify-content-center align-items-center"
                        >Read</ToggleButton>
                        <ToggleButton
                            id={"category-discarded"}
                            value={"discarded"}
                            variant="outline-primary"
                            type="radio"
                            name="category"
                            checked={categoryFilter === "discarded"}
                            onChange={() => setCategoryFilter("discarded")}
                            style={{ width: "25%", borderRadius: "0 0.5rem 0.5rem 0" }}
                            className="d-flex justify-content-center align-items-center"
                        >Discarded</ToggleButton>
                    </InputGroup>
                    <Button className="mt-4 w-100" style={{ height: "3.5rem" }} onClick={() => {
                        setShowFilter(false);
                        setPage(0);
                        setReload(r => !r);
                    }}>
                        Apply
                    </Button>
                </Offcanvas.Body>
            </Offcanvas>

        </Container>
    );
};

export default MessagesPage;