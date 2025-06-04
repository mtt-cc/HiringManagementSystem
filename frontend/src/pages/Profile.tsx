import {useContext, useEffect, useState} from 'react';
import {AuthContext, NavigationContext} from "../Context.tsx";
import {Button, Col, Container, Row} from "react-bootstrap";
import {deleteContact, getContacts, postContact} from "../api/crm.ts";
// import {logout} from "../api/auth.ts";

function ProfilePage() {
    const {user} = useContext(AuthContext);
    const navigation = useContext(NavigationContext);

    const [response, setResponse] = useState<object | null>(null);

    useEffect(() => {
        if (user === undefined || !user.status) {
            navigation.routes.home.to();
        }
    }, [user]);

    if (user !== undefined) {
        return (
            <Container className="d-flex flex-column">
                <Row className="p-5 mt-5">
                    <h1 className="mb-2 text-primary">Profile</h1>
                    <ul>
                        <li>
                            <b>Status:</b> {user.status ? "Logged in" : "Logged out"}
                        </li>
                        <li>
                            <b>Name:</b> {user.name}
                        </li>
                        <li>
                            <b>Full name:</b> {user.fullname}
                        </li>
                        <li>
                            <b>Email:</b> {user.email}
                        </li>
                        <li>
                            <b>Email verified:</b> {user.email_verified ? "Yes" : "No"}
                        </li>
                        <li>
                            <b>Roles:</b> {user.roles.join(", ")}
                        </li>
                    </ul>
                    {
                        response !== null &&
                        <Row className="mt-5">
                            <Col>
                                <h2>Response</h2>
                                <pre>
                                    {JSON.stringify(response, null, 2)}
                                </pre>
                            </Col>
                        </Row>
                    }
                </Row>

                {/* <Row className="p-5 mt-5">
                    <h1 className="mb-2 text-primary">Endpoints</h1>
                    <ul>
                        <li>
                            <b>Login:</b> {user.loginUrl}
                        </li>
                        <li>
                            <b>Logout:</b> {user.logoutUrl}
                        </li>
                    </ul>
                </Row> */}
                <Button
                    // onClick={() => logout(user?.logoutUrl, user?.xsrfToken, console.log, console.error)}
                    onClick={() => window.location.href = user?.logoutUrl}
                    className="w-25 align-self-center mt-5"
                >
                    Logout
                </Button>

            </Container>
        );
    }
}

export default ProfilePage;