import {Offcanvas} from "react-bootstrap";
import EditableParagraph from "./EditableParagraph.tsx";
import * as Yup from "yup";
import {JobOfferStatus} from "../types/JobOffer.ts";

function JobOfferHistory(props: {
    notes: string,
    show: boolean,
    onHide: () => void,
    onSave: (note: string) => void,
    status: JobOfferStatus
}) {
    return (
        <Offcanvas
            show={props.show}
            onHide={props.onHide}
            placement="end"
            style={{width: "40rem"}}
        >
            <Offcanvas.Header closeButton>
                <Offcanvas.Title>Candidate notes</Offcanvas.Title>
            </Offcanvas.Header>
            <Offcanvas.Body>
                <EditableParagraph
                    title={""}
                    body={props.notes}
                    validationSchema={Yup.object({body: Yup.string()})}
                    onChange={
                        (body) => props.onSave(body)
                    }
                    disabled={props.status === JobOfferStatus.ABORTED}
                />
            </Offcanvas.Body>
        </Offcanvas>
    );
}

export default JobOfferHistory;