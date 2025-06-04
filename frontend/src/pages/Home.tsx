import '../App.css';
import {AuthContext, NavigationContext} from "../Context.tsx";
import {useContext, useEffect} from "react";
import {Button, Container} from "react-bootstrap";

function HomePage() {
    const {user} = useContext(AuthContext);
    const navigation = useContext(NavigationContext);

    useEffect(() => {
        if (user !== undefined && user.status) {
            navigation.routes.dashboard.to();
        }
    }, [user]);

    if (user !== undefined) {
        return (
            <Container
                className="d-flex flex-column align-items-center mt-5"
            >
                <h1 className="text-primary mt-5 mb-5">Welcome to Temporary Job Placement Inc.</h1>
                <p className="mt-5 text-decoration-underline">Please login to continue</p>
                <Button
                    onClick={() => {
                        window.location.href = user.loginUrl;
                    }}
                    className="w-25 align-self-center "
                >
                    Login
                </Button>
            </Container>
        )
    }
}

export default HomePage;