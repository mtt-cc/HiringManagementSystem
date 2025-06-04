import {
    JobOfferStatusColorMapping as JOColors,
    JobOfferHistoryEntry,
    jobOfferStatusToString,
    parseDateTime
} from "../types/JobOffer.ts";
import {Col, Container, Offcanvas, Row} from "react-bootstrap";
import {FaArrowRightLong} from "react-icons/fa6";
import React from "react";

function JobOfferHistory(props: {
    title: string,
    history: JobOfferHistoryEntry[],
    show: boolean,
    onHide: () => void
}) {
    return (
        <Offcanvas
            show={props.show}
            onHide={props.onHide}
            placement="end"
            style={{width: "60rem"}}
        >
            <Offcanvas.Header closeButton>
                <Offcanvas.Title className="w-100 text-center">History for {props.title}</Offcanvas.Title>
            </Offcanvas.Header>
            <Offcanvas.Body>
                <Container>
                    {props.history.map((entry, index) => (
                        <JobOfferHistoryLine key={index} entry={entry}/>
                    ))}
                </Container>
            </Offcanvas.Body>
        </Offcanvas>
    );
}

function JobOfferHistoryLine(props: { entry: JobOfferHistoryEntry }) {
    const {date, time} = parseDateTime(props.entry.date_time);
    return (
        <Container className="p-2 m-2">
            <Row>
                {
                    props.entry.previous_status !== null && props.entry.previous_status !== undefined
                        ?
                        <>
                            <Col className="col-3 align-self-center p-2 text-center" style={{
                                backgroundColor: JOColors.getBg(props.entry.previous_status),
                                color: JOColors.getColor(props.entry.previous_status),
                                borderRadius: ".5rem"
                            }}>
                                <p className="fw-bold m-0">{jobOfferStatusToString(props.entry.previous_status)}</p>
                            </Col>
                            <Col className="col-1 d-flex justify-content-center align-content-center pt-2">
                                <FaArrowRightLong size="1.5rem"/>
                            </Col>
                            <Col className="col-3 align-self-center p-2 text-center" style={{
                                backgroundColor: JOColors.getBg(props.entry.current_status),
                                color: JOColors.getColor(props.entry.current_status),
                                borderRadius: ".5rem"
                            }}>
                                <p className="fw-bold m-0">{jobOfferStatusToString(props.entry.current_status)}</p>
                            </Col>
                            <Col className="col-5 align-self-center">
                                <p className="m-0 text-center">{date} - {time}</p>
                            </Col>
                        </>
                        :
                        <>
                            <Col className="col-3 align-self-center p-2 text-center" style={{
                                backgroundColor: JOColors.getBg(props.entry.current_status),
                                color: JOColors.getColor(props.entry.current_status),
                                borderRadius: ".5rem"
                            }}>
                                <p className="fw-bold m-0">{jobOfferStatusToString(props.entry.current_status)}</p>
                            </Col>
                            <Col className="col-5 align-self-center">
                                <p className="m-0 text-center">{date} - {time}</p>
                            </Col>
                        </>
                }
            </Row>


        </Container>
    );
}

export default JobOfferHistory;