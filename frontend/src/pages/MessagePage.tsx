import { useLocation } from 'react-router-dom';
import {Button, Card, Col, Container, Form, ListGroup, Modal, Row, Spinner} from "react-bootstrap";
import ErrorModalRFC7807, {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";
import Message from "../components/Message.tsx";
import {useContext, useEffect, useState} from "react";
import {AttachmentType, MessageType} from "../types/Message.ts";
import {getAttachment, getAttachmentData, updateName, uploadFile} from "../api/document_store.ts";
import {formatBytes, formatTimestamp} from "../utils.tsx";
import {Configuration} from "../api/configuration_document_store.ts";
import BasicTextInput from "../components/form/BasicTextInput.tsx";
import {AuthContext} from "../Context.tsx";
function MessagePage() {
    const location = useLocation();
    const message = location.state?.state.message as MessageType;
    const {user} = useContext(AuthContext);

    const [attachments, setAttachments] = useState<AttachmentType[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<ErrorRFC7807 | null>(null);
    const [reload, setReload] = useState(true);

    const [editNameId, setEditNameId] = useState<number | null>(null);
    const [nameVal, setNameVal] = useState<string>("");
    const [nameTouched, setNameTouched] = useState<boolean>(false);
    const [nameError, setNameError] = useState<string>("");

    const validateName = (name: string) => {
        if (name.trim().length == 0) {
            setNameError("Name is required");
            return false;
        }

        setNameError("");
        return true;
    }

    const fetchAttachments = async (attachmentIds: number[]): Promise<AttachmentType[]> => {
        try {
            const attachmentPromises = attachmentIds.map(async (id) => {
                const metadata = await getAttachment(id);
                return {
                    id: metadata.id,
                    name: metadata.name,
                    type: metadata.contentType,
                    size: metadata.size,
                    date: metadata.creationTimeStamp,
                };
            });
            return await Promise.all(attachmentPromises);
        } catch (e) {
            setError(e as ErrorRFC7807);
            return [];
        }
    };

    useEffect(() => {
        if (reload) {
            if (message?.attachments && message.attachments.length > 0) {
                fetchAttachments(message.attachments).then(setAttachments).finally(() => setIsLoading(false));
            } else {
                setIsLoading(false);
            }
            setReload(false)
        }
    }, [message, reload]);

    if (isLoading) {
        return (
            <Container className="mt-3 d-flex justify-content-center">
                <Card className="mt-5" style={{ width: "20rem" }}>
                    <Card.Body className="d-flex justify-content-center">
                        <h4 className="me-2 mb-0">Loading...</h4>
                        <Spinner variant="primary" animation="border" />
                    </Card.Body>
                </Card>
            </Container>
        );
    }

    if (!message) {
        return <div>No message data available</div>;
    }

    return (
        <Container className="mt-3">
            {error !== null && <ErrorModalRFC7807 error={error} onHide={() => setError(null)} show={true} />}
            <Message MessageData={message} />
            {attachments.length > 0 && (
                <Card className="mt-4">
                    <Card.Header>Attachments</Card.Header>
                    <Card.Body>
                        <ListGroup>
                            {attachments.map((attachment) => {
                                return (
                                    <ListGroup.Item key={attachment.id}>
                                        <Row>
                                            <Col className="col-8">
                                                <p>
                                                    <span
                                                        className="fw-bold">{attachment.name}</span> - <span>({attachment.type})</span>
                                                </p>
                                            </Col>
                                            <Col className="col-4 d-flex justify-content-end p-2">
                                                <Button onClick={() => {
                                                    setEditNameId(attachment.id)
                                                    setNameVal(attachment.name)
                                                }}>
                                                    Edit name
                                                </Button>
                                            </Col>
                                        </Row>
                                        <Row>
                                            <Col className="col-8">
                                                <p>
                                                    <span>Created at: {formatTimestamp(attachment.date)} - size: {formatBytes(attachment.size)}</span>
                                                </p>
                                            </Col>
                                            <Col className="col-4 d-flex justify-content-end p-2">
                                                <Button variant="success" onClick={() => window.open(Configuration.routes.document_store.get.documentData(attachment.id), '_blank', 'noopener,noreferrer')}>
                                                    Download
                                                </Button>
                                            </Col>
                                        </Row>
                                    </ListGroup.Item>
                                )
                            })}
                        </ListGroup>
                    </Card.Body>
                </Card>
            )}
            <Modal
                onHide={() => {
                    setNameError("")
                    setNameTouched(false)
                    setNameVal("")
                    setEditNameId(null)
                }}
                show={editNameId !== null}
            >
                <Modal.Header closeButton>
                    <Modal.Title>Change attachment name</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <BasicTextInput
                        name="new-name"
                        label="Attachment Name"
                        value={nameVal}
                        touched={nameTouched}
                        error={nameError}
                        onChange={(e) => {
                            setNameVal(e.target.value);
                        }}
                        onBlur={() => {
                            if (!nameTouched) setNameTouched(true)
                        }} />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="danger" onClick={() => {
                        setNameError("")
                        setNameTouched(false)
                        setNameVal("")
                        setEditNameId(null)
                    }}>
                        Cancel
                    </Button>
                    <Button variant="success" onClick={() => {
                        if (validateName(nameVal)) {
                            updateName(
                                editNameId!!,
                                nameVal,
                                user?.xsrfToken!!,
                                () => {
                                    setReload(true)
                                },
                                (err) => {
                                    setError(err)
                                },
                                () => {
                                    setNameError("")
                                    setNameTouched(false)
                                    setNameVal("")
                                    setEditNameId(null)
                                }
                            )
                        }
                    }}>
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
}

export default MessagePage;