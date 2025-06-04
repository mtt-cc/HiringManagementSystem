import React, { useEffect, useState } from "react";
import { MessageType } from "../types/Message.ts";
import { Card, Container, Row, Spinner } from "react-bootstrap";
import { getMessages } from "../api/crm.ts";
import { ErrorRFC7807 } from "../components/ErrorModalRFC7807.tsx";
import DOMPurify from 'dompurify';

const NewMessagesWidget: React.FC = () => {
    const [messages, setMessages] = useState<MessageType[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<ErrorRFC7807 | null>(null);

    // Fetch messages and limit to last 5
    useEffect(() => {
        const fetchMessages = () => {
            setIsLoading(true);
            getMessages(
                0,
                100,
                "descending",
                "received",
                (data) => {
                    // Sort messages by timestamp and take the last 5
                    const sortedMessages = data.content.sort(
                        (a: MessageType, b: MessageType) => new Date(a.date).getTime() - new Date(b.date).getTime()
                    );
                    setMessages(sortedMessages.slice(0, 5));
                    setIsLoading(false);
                },
                (err: ErrorRFC7807) => setError(err)
            );
        }

        fetchMessages()
        const intervalId = setInterval(fetchMessages, 5000);
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
        return <p>Error loading messages: {error.detail}</p>;
    }

    if (messages.length === 0) {
        return (
            <Container className="mt-3">
                <p className="text-center">No messages found</p>
            </Container>
        );
    }

    return (
        <Container className="mt-3">
            <Row>
                {messages.map((message) => (
                    <Row key={message.id}>
                        <Card className="m-1">
                            <Card.Body>
                                <Card.Title>{message.subject}</Card.Title>
                                <Card.Subtitle className="mb-2 text-muted">
                                    From: {message.sender}
                                </Card.Subtitle>
                                <Card.Text>
                                    <div className="border p-2" dangerouslySetInnerHTML={{
                                        __html: DOMPurify.sanitize(message.body)
                                    }}/>
                                </Card.Text>
                                <Card.Footer className="text-muted">
                                    {new Date(message.date).toLocaleString()}
                                </Card.Footer>
                            </Card.Body>
                        </Card>
                    </Row>
                ))}
            </Row>
        </Container>
    );
};

export default NewMessagesWidget;