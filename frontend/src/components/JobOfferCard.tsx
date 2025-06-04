import {
    JobOfferHeader,
    JobOfferStatus,
    jobOfferStatusToString,
    JobOfferStatusColorMapping as JOColors, stringToJobOfferStatus
} from "../types/JobOffer.ts";
import {Button, Card, Col, Row} from "react-bootstrap";
import {IoOpenOutline} from "react-icons/io5";

function JobOfferStatusBadge(props: {
    status: JobOfferStatus
}) {
    return (
        <div
            className="d-inline-block"
            style={{
                backgroundColor: JOColors.getBg(props.status),
                color: JOColors.getColor(props.status),
                padding: "0.4em 0.5em",
                borderRadius: "0.25em"
            }}
        >{jobOfferStatusToString(props.status)}</div>
    );
}




function JobOfferCard(props: {
    jobOffer: JobOfferHeader,
    goToJobOffer: (id: number) => void,
    goToCustomer: (id: number) => void,
    className?: string,
    searchKey?: string
}) {
    return (
        <Card className={props.className}>
            <Card.Header>
                <Row className="d-flex align-content-center">
                    <Col style={{paddingTop: ".25rem"}}>
                        <Card.Title>{props.jobOffer.title}</Card.Title>
                    </Col>
                    <Col className="col-auto d-flex align-content-center">
                        <JobOfferStatusBadge status={stringToJobOfferStatus(props.jobOffer.status)}/>
                    </Col>
                </Row>
            </Card.Header>
            <Card.Body>
                <Card.Text>
                    <p className="w-100 text-end mb-0 pt-1"><span
                        className="fw-bold">Customer</span>: {props.jobOffer.customer.first_name + " " + props.jobOffer.customer.last_name} <IoOpenOutline
                        size={"1.8rem"}
                        onClick={() => props.goToCustomer(props.jobOffer.customer.id)}
                        className="ms-2 pb-1"
                        style={{
                            cursor: "pointer",
                        }}
                    /></p>
                </Card.Text>
                <Card.Text>
                    <p>{props.jobOffer.description}</p>
                </Card.Text>
            </Card.Body>
            <Card.Footer>
                <Col className="d-flex justify-content-end">
                    <Button variant="primary" className="w-25"
                            onClick={() => props.goToJobOffer(props.jobOffer.id)}>Details</Button>
                </Col>
            </Card.Footer>
        </Card>
    );
}

export default JobOfferCard;