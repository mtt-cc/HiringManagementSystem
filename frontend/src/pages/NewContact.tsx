import {Container, Card, Button} from "react-bootstrap";
import {
    Address,
    Category, cleanToContact, cleanToCustomer, cleanToProfessional,
    ContactType,
    CustomerAdditionalType, CustomerType,
    Email, EmploymentState,
    ProfessionalAdditionalType, ProfessionalType,
    Telephone
} from "../types/Contact.ts";
import {postContact} from "../api/crm.ts";
import {useContext, useEffect, useState} from "react";
import {AuthContext, NavigationContext} from "../Context.tsx";
import ErrorModalRFC7807, {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";
import ContactForm from "../components/ContactForm.tsx";
import {Skill} from "../types/Skill.ts";

// Define the initial values for the form
const initialValues = {
    id: -1,
    first_name: '',
    last_name: '',
    category: Category.UNKNOWN,
    ssn: '',
    emails: [] as Email[],
    addresses: [] as Address[],
    phone_numbers: [] as Telephone[],
    preferences: '',
    notes: '',
    daily_rate: 0,
    location: '',
    skills: [] as Skill[],
    employment_state: EmploymentState.UNEMPLOYED,
} as ContactType & CustomerAdditionalType & ProfessionalAdditionalType;

function NewContact() {
    const navigation = useContext(NavigationContext);
    const {user} = useContext(AuthContext);

    const [error, setError] = useState<ErrorRFC7807 | null>(null);

    useEffect(() => {
        if (user === undefined || !user.status) {
            navigation.routes.home.to();
        }
    }, [user]);


    const handleSubmit = async (values: ContactType & CustomerAdditionalType & ProfessionalAdditionalType, {setSubmitting}: any) => {
        switch (values.category) {
            case Category.CUSTOMER:
                postContact(
                    cleanToCustomer(values) as CustomerType,
                    user?.xsrfToken!!,
                    () => navigation.routes.contacts.to(),
                    (error: ErrorRFC7807) => setError(error),
                    () => setSubmitting(false)
                );
                break;
            case Category.PROFESSIONAL:
                postContact(
                    cleanToProfessional(values) as ProfessionalType,
                    user?.xsrfToken!!,
                    () => navigation.routes.contacts.to(),
                    (error: ErrorRFC7807) => setError(error),
                    () => setSubmitting(false)
                );
                break;
            case Category.UNKNOWN:
                postContact(
                    cleanToContact(values) as ContactType,
                    user?.xsrfToken!!,
                    () => navigation.routes.contacts.to(),
                    (error: ErrorRFC7807) => setError(error),
                    () => setSubmitting(false)
                );
        }
    };

    return (
        <Container className="mt-3">
            {error !== null && <ErrorModalRFC7807 error={error} onHide={() => setError(null)} show={true}/>}
            <Card>
                <Card.Header className="d-flex">
                    <Button variant="outline-primary" style={{width: "7.5rem"}} onClick={() => {
                        navigation.navigate!!(-1)
                    }}>
                        Go back
                    </Button>
                    <h2 className="text-primary text-center w-100">
                        New Contact
                    </h2>
                    <div style={{width: "7.5rem"}} />
                </Card.Header>
                <Card.Body>
                    <ContactForm
                        initialValues={initialValues} handleSubmit={handleSubmit} submitLabel={"Create contact"} />
                </Card.Body>
            </Card>
        </Container>
    );
}

export default NewContact;