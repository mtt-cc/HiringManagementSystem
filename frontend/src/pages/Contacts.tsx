import {useContext, useEffect, useState} from 'react';
import {
    Button,
    Card,
    Col,
    Container,
    Form,
    InputGroup,
    Offcanvas,
    Row,
    Spinner,
    ToggleButton
} from "react-bootstrap";
import {ContactList} from "../components/ContactList.tsx";
import {ContactType} from "../types/Contact.ts";
import {AuthContext, NavigationContext} from "../Context.tsx";
import {getContacts} from "../api/crm.ts";
import ErrorModalRFC7807, {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";
import {Pageable} from "../types/Pageable.ts";
import PageBar from "../components/PageBar.tsx";

function Contacts() {
    const navigation = useContext(NavigationContext);
    const {user} = useContext(AuthContext);
    const [contacts, setContacts] = useState<Pageable<ContactType>>();
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [error, setError] = useState<ErrorRFC7807 | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [showFilters, setShowFilter] = useState(false);

    type ContactCategoryFilter = "all" | "customer" | "professional" | "unknown";
    const [categoryFilter, setCategoryFilter] = useState<ContactCategoryFilter>("all");
    const [country, setCountry] = useState<string>("");
    const [city, setCity] = useState<string>("");
    const [first, setFirst] = useState<string>("");
    const [last, setLast] = useState<string>("");
    const [ssn, setSsn] = useState<string>("");
    const [reload, setReload] = useState(false);

    useEffect(() => {
        if (user === undefined || !user.status) {
            navigation.routes.home.to();
        }
    }, [user]);

    useEffect(() => {
        setIsLoading(true);
        getContacts(
            page,
            size,
            categoryFilter,
            country.trim(),
            city.trim(),
            first.trim(),
            last.trim(),
            ssn.trim(),
            (data: Pageable<ContactType>) => setContacts(data),
            (err: ErrorRFC7807) => setError(err),
            () => setIsLoading(false)
        )
    }, [page, size, reload]);

    if (isLoading) {
        return (
            <Container className="mt-3 d-flex justify-content-center">
                <Card className="mt-5" style={{width: "20rem"}}>
                    <Card.Body className="d-flex justify-content-center">
                        <h4 className="me-2 mb-0">Loading...</h4> <Spinner variant="primary" animation="border"/>
                    </Card.Body>
                </Card>
            </Container>
        );
    }

    return (
        <Container className="mt-3">
            {error !== null && <ErrorModalRFC7807 error={error} onHide={() => setError(null)} show={true}/>}

            {/* Title and New Contact Button */}
            <Row>
                <Col className="col-2">
                    <InputGroup style={{position: "relative", left: "1rem"}}>
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
                    <Button className="w-25 me-3" variant="primary" onClick={() => navigation.routes.newContact.to()}>
                        New Contact
                    </Button>
                </Col>
            </Row>


            {/* The list of contacts */}
            {
                contacts !== undefined ?
                    <>
                        <ContactList contacts={contacts.content}/>
                        <Row>
                            <Col className="d-flex justify-content-center">
                                {
                                    contacts.totalPages > 1 ?
                                        <PageBar
                                            pageNumber={contacts.pageable.pageNumber}
                                            totalPages={contacts.totalPages}
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

            {/*<ContactFilters />*/}
            <Offcanvas placement="end" style={{width: "45rem"}} show={showFilters} onHide={() => setShowFilter(false)}>
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>Filters</Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>
                    <InputGroup>
                        <ToggleButton
                            id={"category-all"}
                            value={"all"}
                            variant="outline-primary"
                            type="radio"
                            name="category"
                            checked={categoryFilter === "all"}
                            onChange={() => setCategoryFilter("all")}
                            style={{width: "25%", height: "3.5rem", borderRadius: "0.5rem 0 0 0.5rem"}}
                            className="d-flex justify-content-center align-items-center"
                        >All</ToggleButton>
                        <ToggleButton
                            id={"category-customer"}
                            value={"customer"}
                            variant="outline-primary"
                            type="radio"
                            name="category"
                            checked={categoryFilter === "customer"}
                            onChange={() => setCategoryFilter("customer")}
                            style={{width: "25%"}}
                            className="d-flex justify-content-center align-items-center"
                        >Customer</ToggleButton>
                        <ToggleButton
                            id={"category-professional"}
                            value={"professional"}
                            variant="outline-primary"
                            type="radio"
                            name="category"
                            checked={categoryFilter === "professional"}
                            onChange={() => setCategoryFilter("professional")}
                            style={{width: "25%"}}
                            className="d-flex justify-content-center align-items-center"
                        >Professional</ToggleButton>
                        <ToggleButton
                            id={"category-unknown"}
                            value={"unknown"}
                            variant="outline-primary"
                            type="radio"
                            name="category"
                            checked={categoryFilter === "unknown"}
                            onChange={() => setCategoryFilter("unknown")}
                            style={{width: "25%", borderRadius: "0 0.5rem 0.5rem 0"}}
                            className="d-flex justify-content-center align-items-center"
                        >Unknown</ToggleButton>
                    </InputGroup>
                    <Form.Group className="mt-4">
                        <Form.FloatingLabel
                            controlId="countries"
                            label="Country"
                        >
                            <Form.Control
                                name="countries"
                                placeholder="Enter the countries separated by commas"
                                value={country}
                                onChange={(e) => setCountry(e.target.value)}
                            />
                        </Form.FloatingLabel>
                    </Form.Group>
                    <Form.Group className="mt-4">
                        <Form.FloatingLabel
                            controlId="cities"
                            label="City"
                        >
                            <Form.Control
                                name="cities"
                                placeholder="Enter the cities separated by commas"
                                value={city}
                                onChange={(e) => setCity(e.target.value)}
                            />
                        </Form.FloatingLabel>
                    </Form.Group>
                    <Row className="mt-4">
                        <Col className="col-4">
                            <Form.FloatingLabel
                                controlId="first"
                                label="First Name"
                            >
                                <Form.Control
                                    name="first"
                                    placeholder="Enter the first name"
                                    value={first}
                                    onChange={(e) => setFirst(e.target.value)}
                                />
                            </Form.FloatingLabel>
                        </Col>
                        <Col className="col-4">
                            <Form.FloatingLabel
                                controlId="last"
                                label="Last Name"
                            >
                                <Form.Control
                                    name="last"
                                    placeholder="Enter the last name"
                                    value={last}
                                    onChange={(e) => setLast(e.target.value)}
                                />
                            </Form.FloatingLabel>
                        </Col>
                        <Col className="col-4">
                            <Form.FloatingLabel
                                controlId="ssn"
                                label="SSN"
                            >
                                <Form.Control
                                    name="ssn"
                                    placeholder="Enter the SSN"
                                    value={ssn}
                                    onChange={(e) => setSsn(e.target.value)}
                                />
                            </Form.FloatingLabel>
                        </Col>
                    </Row>
                    <Row className="mt-4">
                        <Col className="col-3">
                            <Button className="m-1 w-100" variant="danger" style={{height: "3.5rem"}} onClick={() => {
                                setCategoryFilter("all")
                                setCountry("")
                                setCity("")
                                setFirst("")
                                setLast("")
                                setSsn("")
                            }}>
                                Reset
                            </Button>
                        </Col>
                        <Col className="col-9">
                            <Button className="m-1 w-100" style={{height: "3.5rem"}} onClick={() => {
                                setShowFilter(false)
                                setPage(0)
                                setReload(!reload)
                            }}>
                                Apply
                            </Button>
                        </Col>
                    </Row>
                </Offcanvas.Body>
            </Offcanvas>
        </Container>
    );
}

export default Contacts;