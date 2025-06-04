import {Card, Button, Container, Row, Col} from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import {MessageType} from '../types/Message';
import {formatDate} from "../utils.tsx";
import './css/MessageList.css';

function removeHtmlTagsAndLimit(html, limit) {
    const open = /<([a-zA-Z][a-zA-Z0-9]*)\b[^>]*>/g
    const close = /<\/([a-zA-Z][a-zA-Z0-9]*)\b[^>]*>/g
    const openClose = /<([a-zA-Z][a-zA-Z0-9]*)\b[^>]*>.*<\/\1>/g
    const br = /<br\s*\/?>/g;

    return html.replace(br, " ").replace(open, '').replace(close, '').replace(openClose, '').substring(0, limit);
}

// the single item of the message list
const MessageListItem: React.FC<{
    message: MessageType,
    discardMessage: (id: number) => void,
    onMessageClick: (message: MessageType) => void
}> = ({message, discardMessage, onMessageClick}) => {
    let truncatedBody =  removeHtmlTagsAndLimit(message.body, 100);
    truncatedBody = truncatedBody.length > 10000 ? truncatedBody.substring(0, 100) + '...' : truncatedBody;

    const timestamp = formatDate(message.date);

    const handleClick = () => {
        onMessageClick(message);
    };

    return (
        <Card className={`mb-3 ${message.actual_message_state.value === 'READ' ? 'read-message' : ''}
                                                    ${message.actual_message_state.value === 'DISCARDED' ? 'discarded-message' : ''}
                                                    ${message.actual_message_state.value === 'RECEIVED' ? 'received-message' : ''}`}
              onClick={handleClick} style={{cursor: 'pointer'}}>
            <Card.Body>
                <Row>
                    <Col md={8}>
                        <Card.Title>{message.subject}</Card.Title>
                        <Card.Subtitle className="mb-2 text-muted">from: {message.sender} | received
                            on {timestamp.date} at {timestamp.time} </Card.Subtitle>
                        <Card.Text>{truncatedBody}</Card.Text>
                    </Col>
                    <Col md={4} className="d-flex justify-content-end align-items-center">
                        {message.actual_message_state.value !== 'DISCARDED' && (
                            <Button variant="danger" onClick={(e) => {
                                e.stopPropagation();
                                discardMessage(message.id);
                            }}>Discard</Button>
                        )}
                    </Col>
                </Row>
            </Card.Body>
        </Card>
    );
};

const MessageList: React.FC<{
    messages: MessageType[],
    discardMessage: (id: number) => void,
    onMessageClick: (message: MessageType) => void
}> = ({messages, discardMessage, onMessageClick}) => {
    return (
        <Container>
            <Row className="justify-content-center">
                <Col md={8}>
                    {messages.map((message) => (
                        <MessageListItem key={message.id} message={message} discardMessage={discardMessage}
                                         onMessageClick={onMessageClick}/>
                    ))}
                </Col>
            </Row>
        </Container>
    );
};

export {MessageListItem, MessageList};