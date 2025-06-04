import {useContext, useEffect, useState} from "react";
import {useParams} from 'react-router-dom';
import {
    Badge,
    Button,
    Card,
    Col,
    Container,
    ListGroup,
    Modal,
    Offcanvas,
    Row,
    Spinner,
    Toast,
    ToastContainer
} from "react-bootstrap";
import {AuthContext, NavigationContext} from "../Context.tsx";
import ErrorModalRFC7807, {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";
import {
    Category,
    cleanToContact,
    cleanToCustomer,
    cleanToProfessional,
    ContactType,
    CustomerAdditionalType,
    CustomerType,
    parseCategory,
    parseEmploymentState,
    ProfessionalAdditionalType,
    ProfessionalType
} from "../types/Contact.ts";
import {deleteContact, getContact, updateContact} from '../api/crm';
import {Skill} from "../types/Skill.ts";
import {CategoryBadge} from "../components/ContactList.tsx";
import {IoCopyOutline, IoOpenOutline} from "react-icons/io5";
import {FaCheck, FaPlus} from "react-icons/fa";
import BadgeList, {BadgeEntry} from "../components/BadgeList.tsx";
import ContactForm from "../components/ContactForm.tsx";
import {
    JobOfferHeader,
    JobOfferStatusColorMapping as JOColors,
    jobOfferStatusToString,
    stringToJobOfferStatus
} from "../types/JobOffer.ts";
import {JobOfferStatusBadge} from "../components/JobOfferCard.tsx";

function Contact() {
    const {id} = useParams<{ id: string }>();
    const {user} = useContext(AuthContext);
    const navigation = useContext(NavigationContext);
    const [loading, setLoading] = useState(true);
    const [contact, setContact] = useState<ContactType | CustomerType | ProfessionalType | null>(null);
    const [error, setError] = useState<ErrorRFC7807 | null>(null);
    const [deleteConfirm, setDeleteConfirm] = useState(false);
    const [copied, setCopied] = useState(false);
    const [copiedResult, setCopiedResult] = useState<boolean | undefined>(undefined);
    const [editing, setEditing] = useState(false);
    const [dirty, setDirty] = useState(true);

    useEffect(() => {
        if (user === undefined || !user.status) {
            navigation.routes.home.to();
        }
    }, [user]);

    useEffect(() => {
        if (dirty) {
            if (user !== null && id !== undefined) {
                getContact(
                    id,
                    (data: ContactType | CustomerType | ProfessionalType) => {
                        setContact(data);
                        setDirty(false);
                    },
                    (error: ErrorRFC7807) => setError(error),
                    () => setLoading(false)
                );
            } else {
                setError({
                    title: "Contact id not provided",
                    detail: "Please provide a contact id.",
                    type: "about:blank",
                    instance: "frontend",
                });
            }
        }
    }, [id, dirty]);

    const goToJobOffer = (id: number) => {
        navigation.routes.jobOffer.to(id.toString());
    }

    const handleContactDelete = async () => {
        if (user !== null) {
            if (id !== undefined) {
                setLoading(true);
                deleteContact(
                    id,
                    user!!.xsrfToken,
                    () => navigation.routes.contacts.to(),
                    (error: ErrorRFC7807) => setError(error),
                    () => {
                        setLoading(false);
                        setDeleteConfirm(false)
                    }
                );
            } else {
                setError({
                    title: "Contact id not provided",
                    detail: "Please provide a contact id.",
                    type: "about:blank",
                    instance: "frontend",
                } as ErrorRFC7807);
            }
        }
    };

    const handleEditContact = async (values: ContactType & CustomerAdditionalType & ProfessionalAdditionalType, {setSubmitting}: any) => {
        switch (values.category) {
            case Category.CUSTOMER:
                updateContact(
                    cleanToCustomer({...values, id: contact!!.id}) as CustomerType,
                    user?.xsrfToken!!,
                    () => {
                        setDirty(true)
                        setEditing(false)
                    },
                    (error: ErrorRFC7807) => setError(error),
                    () => setSubmitting(false)
                );
                break;
            case Category.PROFESSIONAL:
                updateContact(
                    cleanToProfessional({...values, id: contact!!.id}) as ProfessionalType,
                    user?.xsrfToken!!,
                    () => {
                        setDirty(true)
                        setEditing(false)
                    },
                    (error: ErrorRFC7807) => setError(error),
                    () => setSubmitting(false)
                );
                break;
            case Category.UNKNOWN:
                updateContact(
                    cleanToContact({...values, id: contact!!.id}) as ContactType,
                    user?.xsrfToken!!,
                    () => {
                        setDirty(true)
                        setEditing(false)
                    },
                    (error: ErrorRFC7807) => setError(error),
                    () => setSubmitting(false)
                );
        }
    };

    const handleCopy = (text: string) => {
        navigator.clipboard.writeText(text).then(() => {
            setCopied(true);
            setCopiedResult(true);
        }).catch(() => {
            setCopied(true);
            setCopiedResult(false);
        })
    }

    if (loading) {
        return (
            <Container className="mt-5 text-center">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </Container>
        );
    }

    return (
        <Container className="mt-3">
            {error !== null && <ErrorModalRFC7807 error={error} onHide={() => setError(null)} show={true}/>}
            <ToastContainer
                position="top-end"
            >
                <Toast
                    autohide
                    delay={3000}
                    show={copied}
                    onClose={() => {
                        setCopied(false)
                        setCopiedResult(undefined)
                    }}
                    style={{
                        position: "relative",
                        top: "3rem",
                        right: "1rem",
                    }}
                >
                    <Toast.Body>{
                        copiedResult ?
                            <div className="d-flex align-content-center">
                                Copied to clipboard
                                <FaCheck size="1rem" style={{color: "green", marginLeft: "1rem"}}/>

                            </div> :
                            <div className="d-flex align-content-center">
                                Failed to copy to clipboard
                                <FaPlus size="1rem"
                                        style={{color: "red", marginLeft: "1rem", transform: "rotate(45deg)"}}/>
                            </div>
                    }</Toast.Body>
                </Toast>
            </ToastContainer>
            <Card>
                {
                    contact !== null ? <>
                            <Card.Header className="d-flex">
                                <Button variant="outline-primary" style={{width: "7.5rem"}} onClick={() => {
                                    navigation.navigate!!(-1)
                                }}>
                                    Go back
                                </Button>
                                <h2 className="text-primary text-center w-100">
                                    Contact details
                                </h2>
                                <div style={{width: "7.5rem"}}/>
                            </Card.Header>
                            <Card.Body>
                                <Container fluid>
                                    <Row>
                                        <Col className="col-auto">
                                            <h2>{contact.first_name} {contact.last_name}</h2>
                                        </Col>
                                        <Col className="d-flex justify-content-end">
                                            <CategoryBadge category={contact.category}/>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col className="d-flex align-content-center ps-5">
                                            SSN: {contact.ssn}
                                            <IoCopyOutline
                                                style={{
                                                    cursor: "pointer",
                                                    position: "relative",
                                                    top: "0.2rem",
                                                    marginLeft: "0.5rem"
                                                }}
                                                onClick={() => handleCopy(contact.ssn)}
                                                size="1.2rem"
                                            />
                                        </Col>
                                    </Row>
                                </Container>
                                <Col>
                                    <Row>
                                        <Col md={6}>
                                            <LimitedItemList
                                                items={contact.addresses.map(address => `${address.street} ${address.number}, ${address.city}, ${address.postal_code}, ${address.country}`)}
                                                title="Addresses"
                                                limit={3}
                                                className="m-3"
                                            />
                                            <LimitedItemList
                                                items={contact.emails.map(email => email.email)}
                                                title="Emails"
                                                limit={3}
                                                className="m-3"
                                                copy
                                                handleCopy={handleCopy}
                                            />
                                            <LimitedItemList
                                                items={contact.phone_numbers.map(phone => phone.telephone)}
                                                title="Phone numbers"
                                                limit={3}
                                                className="m-3"
                                                copy
                                                handleCopy={handleCopy}
                                            />
                                        </Col>
                                        <Col md={6}>
                                            {parseCategory(contact.category) !== Category.UNKNOWN ? (
                                                <>
                                                    <Card className="m-3">
                                                        {parseCategory(contact.category) === Category.PROFESSIONAL && (
                                                            <Card.Body>
                                                                <Card.Title className="mb-3">Additional
                                                                    details:</Card.Title>
                                                                <Row className="ms-5">
                                                                    <Col
                                                                        className="col-4 d-flex align-content-center fw-bold">
                                                                        <Card.Text>Skills:</Card.Text>
                                                                    </Col>
                                                                    <Col>
                                                                        {
                                                                            (contact as ProfessionalType).related_professional.skills.length > 0 ?
                                                                                <BadgeList
                                                                                    left
                                                                                    badges={(contact as ProfessionalType).related_professional.skills.map(skill => ({
                                                                                        key: skill,
                                                                                        text: skill
                                                                                    } as BadgeEntry))}/> :
                                                                                <Card.Text>empty</Card.Text>
                                                                        }
                                                                    </Col>
                                                                </Row>
                                                                <Row className="ms-5 mt-2">
                                                                    <Col
                                                                        className="col-4 d-flex align-content-center fw-bold">
                                                                        <Card.Text>Location:</Card.Text>
                                                                    </Col>
                                                                    <Col>
                                                                        <Card.Text>
                                                                            {(contact as ProfessionalType).related_professional.location}
                                                                        </Card.Text>
                                                                    </Col>
                                                                </Row>
                                                                <Row className="ms-5 mt-2">
                                                                    <Col
                                                                        className="col-4 d-flex align-content-center fw-bold">
                                                                        <Card.Text>Employment State:</Card.Text>
                                                                    </Col>
                                                                    <Col>
                                                                        <Card.Text>
                                                                            {(contact as ProfessionalType).related_professional.employment_state}
                                                                        </Card.Text>
                                                                    </Col>
                                                                </Row>
                                                                <Row className="ms-5 mt-2">
                                                                    <Col
                                                                        className="col-4 d-flex align-content-center fw-bold">
                                                                        <Card.Text>Notes:</Card.Text>
                                                                    </Col>
                                                                    <Col>
                                                                        <Card.Text>
                                                                            {(contact as ProfessionalType).related_professional.notes.length > 0 ? (contact as ProfessionalType).related_professional.notes : "empty"}
                                                                        </Card.Text>
                                                                    </Col>
                                                                </Row>
                                                            </Card.Body>
                                                        )}
                                                        {parseCategory(contact.category) === Category.CUSTOMER && (
                                                            <Card.Body>
                                                                <Card.Title>Additional details:</Card.Title>
                                                                <Row className="ms-5 mt-2">
                                                                    <Col
                                                                        className="col-4 d-flex align-content-center fw-bold">
                                                                        <Card.Text>Preferences:</Card.Text>
                                                                    </Col>
                                                                    <Col>
                                                                        <Card.Text>
                                                                            {(contact as CustomerType).related_customer.notes.length > 0 ? (contact as CustomerType).related_customer.notes : "empty"}
                                                                        </Card.Text>
                                                                    </Col>
                                                                </Row>
                                                                <Row className="ms-5 mt-2">
                                                                    <Col
                                                                        className="col-4 d-flex align-content-center fw-bold">
                                                                        <Card.Text>Notes:</Card.Text>
                                                                    </Col>
                                                                    <Col>
                                                                        <Card.Text>
                                                                            {(contact as CustomerType).related_customer.preferences.length > 0 ? (contact as CustomerType).related_customer.preferences : "empty"}
                                                                        </Card.Text>
                                                                    </Col>
                                                                </Row>
                                                            </Card.Body>
                                                        )}
                                                    </Card>
                                                    <Card className="m-3">
                                                        <Card.Body>
                                                            <Card.Title>Job offers:</Card.Title>
                                                            <ListGroup>
                                                                {
                                                                    parseCategory(contact.category) === Category.CUSTOMER &&
                                                                        <JobOfferList jobOffers={(contact as CustomerType).related_customer.job_offers} goToJobOffer={goToJobOffer}/>
                                                                }
                                                                {
                                                                    parseCategory(contact.category) === Category.PROFESSIONAL &&
                                                                    <JobOfferList jobOffers={(contact as ProfessionalType).related_professional.job_offers} goToJobOffer={goToJobOffer}/>
                                                                }
                                                            </ListGroup>
                                                        </Card.Body>
                                                    </Card>
                                                </>
                                            ) : (
                                                <Card className="m-3">
                                                    <Card.Body>
                                                        <Card.Title>Additional details:</Card.Title>
                                                        <Card.Text>No additional details available</Card.Text>
                                                    </Card.Body>
                                                </Card>
                                            )}
                                        </Col>

                                    </Row>
                                </Col>
                                <Offcanvas
                                    placement="bottom"
                                    show={editing}
                                    style={{height: "90vh"}}
                                    backdrop="static"
                                    onHide={() => setEditing(false)}
                                >
                                    <Offcanvas.Header closeButton>
                                        <Offcanvas.Title className="w-100 text-center text-primary"
                                                         style={{fontSize: "1.5rem"}}>Edit contact</Offcanvas.Title>
                                    </Offcanvas.Header>
                                    <Offcanvas.Body>
                                        <ContactForm
                                            initialValues={{
                                                id: contact.id,
                                                first_name: contact.first_name,
                                                last_name: contact.last_name,
                                                ssn: contact.ssn,
                                                category: parseCategory(contact.category),
                                                addresses: contact.addresses,
                                                emails: contact.emails,
                                                phone_numbers: contact.phone_numbers,
                                                notes: parseCategory(contact.category) === Category.CUSTOMER ?
                                                    (contact as CustomerType).related_customer.notes :
                                                    parseCategory(contact.category) === Category.PROFESSIONAL ?
                                                        (contact as ProfessionalType).related_professional.notes : "",
                                                preferences: parseCategory(contact.category) === Category.CUSTOMER ?
                                                    (contact as CustomerType).related_customer.preferences : "",
                                                daily_rate: parseCategory(contact.category) === Category.PROFESSIONAL ?
                                                    (contact as ProfessionalType).related_professional.daily_rate : 0,
                                                location: parseCategory(contact.category) === Category.PROFESSIONAL ?
                                                    (contact as ProfessionalType).related_professional.location : "",
                                                skills: parseCategory(contact.category) === Category.PROFESSIONAL ?
                                                    (contact as ProfessionalType).related_professional.skills : [] as Skill[],
                                                employment_state: parseCategory(contact.category) === Category.PROFESSIONAL ?
                                                    parseEmploymentState((contact as ProfessionalType).related_professional.employment_state) : undefined
                                            } as ContactType & CustomerAdditionalType & ProfessionalAdditionalType}
                                            handleSubmit={handleEditContact}
                                            submitLabel="Save changes"
                                            editMode
                                        />
                                    </Offcanvas.Body>
                                </Offcanvas>
                            </Card.Body>
                            <Card.Footer>
                                <Col className="d-flex justify-content-end">
                                    <Button className="w-25 me-3" variant="danger" onClick={() => setDeleteConfirm(true)}>
                                        Delete contact
                                    </Button>
                                    <Button className="w-25 me-3" variant="primary" onClick={() => setEditing(true)}>
                                        Edit contact
                                    </Button>
                                </Col>
                                <Modal
                                    show={deleteConfirm}
                                    onHide={() => setDeleteConfirm(false)}
                                    backdrop="static"
                                >
                                    <Modal.Header closeButton>
                                        <Modal.Title>Confirm deletion</Modal.Title>
                                    </Modal.Header>
                                    <Modal.Body>
                                        <p>Are you sure you want to delete this contact? This operation cannot be
                                            undone.</p>
                                    </Modal.Body>
                                    <Modal.Footer>
                                        <Button variant="danger" onClick={() => handleContactDelete()}>
                                            Yes
                                        </Button>
                                        <Button variant="secondary" onClick={() => setDeleteConfirm(false)}>
                                            No
                                        </Button>
                                    </Modal.Footer>
                                </Modal>
                            </Card.Footer>
                        </>
                        :
                        <Card.Body>
                            <Card.Title className="w-100 text-center">
                                Contact not found
                            </Card.Title>
                        </Card.Body>
                }
            </Card>
        </Container>
    );
}

function JobOfferList(props: { jobOffers: JobOfferHeader[], goToJobOffer: (id: number) => void }) {
    const [all, setAll] = useState(false);

    if (props.jobOffers.length === 0) {
        return (
            <ListGroup.Item>
                <Row>
                    <Col>
                        <p>No job offers available</p>
                    </Col>
                </Row>
            </ListGroup.Item>
        )
    }

    if (props.jobOffers.length <= 5) {
        return (
            <>
                {props.jobOffers.map((jobOffer, index) => (
                    <JobOfferEntry key={index} jobOffer={jobOffer} goToJobOffer={props.goToJobOffer}/>
                ))}
            </>
        )
    }

    if (all) {
        return (
            <>
                {props.jobOffers.map((jobOffer, index) => (
                    <JobOfferEntry key={index} jobOffer={jobOffer} goToJobOffer={props.goToJobOffer}/>
                ))}
                <ListGroup.Item>
                    <Row>
                        <Col>
                            <Button variant="primary" onClick={() => setAll(false)}>
                                Show less
                            </Button>
                        </Col>
                    </Row>
                </ListGroup.Item>
            </>
        )
    } else {
        return (
            <>
                {props.jobOffers.slice(0, 5).map((jobOffer, index) => (
                    <JobOfferEntry key={index} jobOffer={jobOffer} goToJobOffer={props.goToJobOffer}/>
                ))}
                <ListGroup.Item>
                    <Row>
                        <Col>
                            <Button variant="primary" onClick={() => setAll(true)}>
                                Show more
                            </Button>
                        </Col>
                    </Row>
                </ListGroup.Item>
            </>
        )
    }
}

function JobOfferEntry(props: { jobOffer: JobOfferHeader, goToJobOffer: (id: number) => void }) {
    const status = stringToJobOfferStatus(props.jobOffer.status);
    return (
        <ListGroup.Item>
            <Row>
                <Col className="col-1 d-flex justify-content-center">
                    <IoOpenOutline
                        size={"1.5rem"}
                        onClick={() => props.goToJobOffer(props.jobOffer!!.id)}
                        style={{
                            cursor: "pointer",
                        }}
                    />
                </Col>
                <Col>
                    <p className="m-0">{props.jobOffer.title}</p>
                </Col>
                <Col className="text-end">
                    <div
                        className="d-inline-block"
                        style={{
                            fontWeight: "bold",
                            fontSize: "0.8rem",
                            backgroundColor: JOColors.getBg(status),
                            color: JOColors.getColor(status),
                            padding: "0.1em 0.2em",
                            borderRadius: "0.25em"
                        }}
                    >{jobOfferStatusToString(status)}</div>
                </Col>
            </Row>
        </ListGroup.Item>
    )
}

function LimitedItemList(props: {
    items: string[],
    title: string,
    limit: number,
    className?: string,
    copy?: boolean,
    handleCopy?: (text: string) => void
}) {
    const [limited, setLimited] = useState(true);

    function CopiableEntry(p: { item: string }) {
        return (
            <div className="d-flex align-content-center">
                {p.item}
                {props.copy && (
                    <IoCopyOutline
                        style={{
                            cursor: "pointer",
                            position: "relative",
                            top: "0.2rem",
                            marginLeft: "0.5rem"
                        }}
                        onClick={() => {
                            if (props.handleCopy) props.handleCopy(p.item)
                        }}
                        size="1.2rem"
                    />
                )}
            </div>
        )
    }

    return (
        <Card className={props.className}>
            <Card.Body>
                <Card.Title>
                    {props.title}
                </Card.Title>
                {
                    props.items.length > 0 ?
                        props.items.length > props.limit ?
                            <>
                                <ul>
                                    {props.items.slice(0, props.limit).map((item, index) => (
                                        <li key={index}>{
                                            props.copy ?
                                                <CopiableEntry item={item}/>
                                                :
                                                item
                                        }</li>
                                    ))}
                                </ul>
                                <ul>
                                    {limited ? null : props.items.slice(props.limit).map((item, index) => (
                                        <li key={index}>{
                                            props.copy ?
                                                <CopiableEntry item={item}/>
                                                :
                                                item
                                        }</li>
                                    ))}
                                </ul>
                                <div className="d-flex justify-content-end">
                                    <Button variant="primary" onClick={() => setLimited(!limited)}>
                                        {limited ? "Show more" : "Show less"}
                                    </Button>
                                </div>
                            </> :
                            <ul>
                                {props.items.map((item, index) => (
                                    <li key={index}>{
                                        props.copy ?
                                            <CopiableEntry item={item}/>
                                            :
                                            item
                                    }</li>
                                ))}
                            </ul>
                        :
                        <p>No {props.title.toLowerCase()} available</p>
                }
            </Card.Body>
        </Card>
    )
}

export default Contact;