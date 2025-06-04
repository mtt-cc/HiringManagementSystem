import {Form} from "react-bootstrap";

function ErrorFeedback(props: { error: string | undefined }) {
    return (
        <Form.Control.Feedback type="invalid">
            <p className="fw-bold ms-3 mb-0">{props.error}</p>
        </Form.Control.Feedback>
    );
}

export default ErrorFeedback;