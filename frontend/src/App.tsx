import './App.css';
import Header from './components/Header';
import Footer from './components/Footer';
import React, {useEffect, useState} from "react";
import {AuthInformation} from "./types/AuthInformation.ts";
import {AuthContext} from "./Context.tsx";
import {fetchMe} from "./api/auth.ts";
import Router from "./pages/Router.tsx";
import ErrorModalRFC7807, {ErrorRFC7807} from "./components/ErrorModalRFC7807.tsx";
import {Card, Container, Spinner} from "react-bootstrap";

export function PageContainer(
    props: {
        title: string;
        children: React.ReactNode;
    }
) {
    const {title, children} = props;
    return (
        <div className="d-flex flex-column min-vh-100">
            <Header pageTitle={title}/>
            <main className="flex-fill">
                {children}
            </main>
            <Footer/>
        </div>
    );
}


function App() {
    const [user, setUser] = useState<AuthInformation | undefined>(undefined);            // this is the correct one when the auth will work
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<ErrorRFC7807 | null>(null);


    useEffect(() => {
        setIsLoading(true);
        fetchMe(
            setUser,
            setError,
            () => setIsLoading(false)
        );
    }, []);

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
        <AuthContext.Provider value={{
            user: user
        }}>
            {error !== null && <ErrorModalRFC7807 error={error} onHide={() => setError(null)} show={true}/>}
            <Router/>
        </AuthContext.Provider>
    );
}

export default App;