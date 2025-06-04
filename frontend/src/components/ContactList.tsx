import {Card, Button, Container, Row, Col, Badge} from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import {ContactType} from '../types/Contact';
import {useContext} from "react";
import {NavigationContext} from "../Context.tsx";

// Single contact card component
function ContactListItem(props: {
    contact: ContactType,
    openDetails: (id: number) => void
}) {
    const {first_name, last_name, emails, phone_numbers, addresses, category} = props.contact;
    const truncatedEmails: string[] = emails.slice(0, 3).map(email => email.email);
    const truncatedPhones: string[] = phone_numbers.slice(0, 3).map(phone => phone.telephone);
    const primaryAddress = addresses.length > 0 ? `${addresses[0].street} ${addresses[0].number}, ${addresses[0].city}, ${addresses[0].postal_code}, ${addresses[0].country}` : "No address provided";

    return (
        <Card className="mb-3">
            <Card.Body>
                <Row>
                    <Col className="col-auto">
                        <Card.Title>{first_name} {last_name}</Card.Title>
                    </Col>
                    <Col className="d-flex justify-content-end">
                        <CategoryBadge category={category}/>
                    </Col>
                </Row>
                {
                    truncatedEmails.length === 0 ?
                        <Card.Text><strong>Email:</strong> No email provided</Card.Text> :
                        emails.length < 4 ?
                            <Card.Text>
                                <strong>Email:</strong> {truncatedEmails.join(', ')}
                            </Card.Text>
                            :
                            <Card.Text>
                                <strong>Email:</strong> {truncatedEmails.join(', ')}...
                            </Card.Text>
                }
                {
                    truncatedPhones.length === 0 ?
                        <Card.Text><strong>Phone:</strong> No phone number provided</Card.Text> :
                        phone_numbers.length < 4 ?
                            <Card.Text>
                                <strong>Phone:</strong> {truncatedPhones.join(', ')}
                            </Card.Text>
                            :
                            <Card.Text>
                                <strong>Phone:</strong> {truncatedPhones.join(', ')}...
                            </Card.Text>
                }
                <Card.Text>
                    <strong>Address:</strong> {primaryAddress}
                </Card.Text>
            </Card.Body>
            <Card.Footer className="d-flex justify-content-end">
                <Button className="w-25" variant="primary"
                        onClick={() => props.openDetails(props.contact.id)}>Details</Button>
            </Card.Footer>
        </Card>
    )
        ;
}

export function CategoryBadge(props: { category: string }) {
    return (
        <div>
            <Badge
                className="ps-3 pe-3 pt-2 pb-2"
                bg={props.category === "Unknown" ? "warning" : "primary"}
                style={{color: props.category === "Unknown" ? "#484848" : "", fontSize: "1rem"}}
            >
                {props.category}
            </Badge>
        </div>
    )
}

// Contact list component
function ContactList(props: { contacts: ContactType[] }) {
    const navigation = useContext(NavigationContext);

    // Navigate to the details page when the button is clicked
    const openDetails = (id: number) => {
        navigation.routes.contact.to(id);
    };

    if (props.contacts.length === 0) {
        return (
            <Container fluid>
                <p className="text-center mt-5" style={{fontSize: "1.2rem", textDecoration: "underline"}}>No contacts found</p>
            </Container>
        );
    }

    return (
        <Container fluid>
            {props.contacts.map((contact) => (
                <ContactListItem key={contact.id} contact={contact} openDetails={openDetails}/>
            ))}
        </Container>
    );
}

export {ContactListItem, ContactList};