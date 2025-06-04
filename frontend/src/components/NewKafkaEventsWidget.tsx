import React, { useEffect, useState } from "react";
import { Card, Container, Row, Spinner } from "react-bootstrap";
import { ErrorRFC7807 } from "../components/ErrorModalRFC7807.tsx";

interface Event {
    id: string;
    message: string;
}

const NewKafkaEventsWidget: React.FC = () => {
    const [isLoading, setIsLoading] = useState(true);
    const [events, setEvents] = useState<Event[]>([]);
    const [error, setError] = useState<ErrorRFC7807 | null>(null);

    const fetchMessages = async () => {
        setIsLoading(true);
        try {
            const response = await fetch('http://localhost:9000/api/messages');
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setEvents(data);
            setIsLoading(false);
        } catch (error) {
            setError('Error fetching messages: ' + error.message);
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchMessages(); 

        const intervalId = setInterval(() => {
            fetchMessages(); 
        }, 5000);

        return () => clearInterval(intervalId);
    }, []);

    if (isLoading) {
        return (
            <Container className="mt-3 d-flex justify-content-center">
                <Spinner animation="border" variant="primary" />
            </Container>
        );
    }

    if (error) {
        return <p>Error loading events: {error.detail}</p>;
    }

    if (events.length === 0) {
        return (
            <Container className="mt-3">
                <p className="text-center">No events yet</p>
            </Container>
        );
    }

    return (
        <Container className="m-3">
            <Row>
                {events.map((event) => (
                    <Row key={event.id}>
                        <Card className="m-1">
                            <Card.Body>
                                <Card.Title>{event.message}</Card.Title>
                            </Card.Body>
                        </Card>
                    </Row>
                ))}
            </Row>
        </Container>
    );
};

export default NewKafkaEventsWidget;