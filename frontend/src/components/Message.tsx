import '../App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import {channelToString, MessageType} from '../types/Message';
import { Card, Badge, Button, Container } from 'react-bootstrap';
import {formatDate} from "../utils.tsx";
// @ts-ignore
import DOMPurify from 'dompurify';

function Message(props: { MessageData: MessageType }) {
    const m = props.MessageData;
    const timestamp = formatDate(m.date);

    // depending on the actualMessageState change the color of the dot and text
    const getactualMessageStateColor = (actual_message_state: string) => {
        switch (actual_message_state) {
            case 'RECEIVED':
                return 'text-warning';
            case 'READ':
                return 'text-success';
            case 'DISCARDED':
                return 'text-danger';
            default:
                return 'text-secondary';
        }
    };

    return (
        <Card className="mb-3">
            <Card.Header className="d-flex justify-content-between align-items-center">
                <Container className="d-flex align-items-center">
                    <h2 className="h5 mb-0 me-2">{m.subject}</h2>
                    <Badge className="d-flex align-items-center">
                        <span className={`me-2 ${getactualMessageStateColor(m.actual_message_state.value)}`}>&#9679;</span>
                        <small className={getactualMessageStateColor(m.actual_message_state.value)}>{m.actual_message_state.value}</small>
                    </Badge>
                </Container>
            </Card.Header>
            <Card.Body>
                <Container>
                    <small>Received: {timestamp.date} at {timestamp.time}</small>
                </Container>
                <Container className="d-flex align-items-center mb-2">
                    <p className="mb-0"><strong>Sender:</strong> {m.sender}</p>
                </Container>
                <Container>
                    <p><strong>Channel:</strong> {channelToString(m.channel)}</p>
                    <div className="border p-2" dangerouslySetInnerHTML={{
                        __html: DOMPurify.sanitize(m.body)
                    }}/>
                </Container>
            </Card.Body>
        </Card>
    );
}

export default Message;