import React, { useEffect, useState } from "react";
import { JobOffer, JobOfferStatus, jobOfferStatusToString } from "../types/JobOffer.ts";
import { Card, Container, Row, Spinner } from "react-bootstrap";
import { getJobOffers } from "../api/crm.ts";
import { ErrorRFC7807 } from "../components/ErrorModalRFC7807.tsx";

const JobOffersWidget: React.FC = () => {
    const [jobOffers, setJobOffers] = useState<JobOffer[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<ErrorRFC7807 | null>(null);

    // Fetch job offers with status CREATED and limit to last 5 sorted by date
    useEffect(() => {
        setIsLoading(true);
        getJobOffers(
            0,
            5,
            [jobOfferStatusToString(JobOfferStatus.CREATED)], 
            "",
            [],
            [],
            0,
            0,
            (data) => {
                setJobOffers(data.content as JobOffer[]);
                setIsLoading(false);
            },
            (err: ErrorRFC7807) => setError(err)
        );
    }, []);

    if (isLoading) {
        return (
            <Container className="mt-3 d-flex justify-content-center">
                <Spinner animation="border" variant="primary" />
            </Container>
        );
    }

    if (error) {
        return <p>Error loading job offers: {error.detail}</p>;
    }

    if (jobOffers.length === 0) {
        return (
            <Container className="mt-3">
                <p className="text-center">No job offers found</p>
            </Container>
        );
    }

    return (
        <Container className="m-3">
            <Row>
                {jobOffers.map((jobOffer) => (
                    <Row key={jobOffer.id}>
                        <Card className="m-1">
                            <Card.Body>
                                <Card.Title>{jobOffer.title}</Card.Title>
                                <Card.Subtitle className="mb-2 text-muted">
                                    Status: {jobOffer.status}
                                </Card.Subtitle>
                                <Card.Text>{jobOffer.description}</Card.Text>
                                <Card.Footer className="text-muted">
                                    {jobOffer.customer.category} : {jobOffer.customer.first_name} {jobOffer.customer.last_name}
                                </Card.Footer>
                            </Card.Body>
                        </Card>
                    </Row>
                ))}
            </Row>
        </Container>
    );
};

export default JobOffersWidget;