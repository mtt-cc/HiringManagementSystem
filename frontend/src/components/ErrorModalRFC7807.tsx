import {Button, Modal} from "react-bootstrap";
import {useEffect} from "react";

export type ErrorRFC7807 = {
    title: string,
    detail: string,
    status?: number,
    type?: string,
    instance?: string
}

function ErrorModal(props: { show: boolean, onHide: () => void, error: ErrorRFC7807}) {

    useEffect(() => {
    }, [props.error]);

    return (
        <Modal show={props.show} onHide={props.onHide} style={{zIndex: 100000}}>
            <Modal.Header closeButton>
                <Modal.Title>{props.error.title}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>There was an error processing your request:</p>
                <p className="p-2" style={{border: ".1rem solid lightgray"}}>
                    <code>
                        {props.error.detail}
                    </code>
                </p>
                {props.error.type && <p>Type: <code>{props.error.type}</code></p>}
                {props.error.status && <p>Status: <code>{props.error.status}</code></p>}
                {props.error.instance && <p>Instance: <code>{props.error.instance}</code></p>}
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={props.onHide}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

export default ErrorModal;