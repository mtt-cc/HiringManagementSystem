import '../App.css';
import { useContext, useEffect, useState } from 'react';
import { Col, Container, Row } from "react-bootstrap";
import { AuthContext, NavigationContext } from "../Context.tsx";
import NewKafkaEventsWidget from "../components/NewKafkaEventsWidget.tsx";
import NewJobOffersWidget from "../components/NewJobOffersWidget.tsx";
import { getJobOffers } from "../api/crm.ts";
import { jobOfferStatusToString, JobOfferStatus } from "../types/JobOffer.ts";
import { ErrorRFC7807 } from "../components/ErrorModalRFC7807.tsx";

function ManagerPage() {
    const [error, setError] = useState<ErrorRFC7807 | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const { user } = useContext(AuthContext);
    const navigation = useContext(NavigationContext);
    const [states, setStates] = useState<JobOfferStatus[]>([
        JobOfferStatus.CREATED,
        JobOfferStatus.SELECTION_PHASE,
        JobOfferStatus.CANDIDATE_PROPOSAL,
        JobOfferStatus.CONSOLIDATED,
        JobOfferStatus.DONE,
        JobOfferStatus.ABORTED
    ]);
    const [newJobOffers, setNewJobOffers] = useState(0);
    const [selectionPhaseOffers, setSelectionPhaseOffers] = useState(0);
    const [candidateProposalOffers, setCandidateProposalOffers] = useState(0);
    const [consolidatedOffers, setConsolidatedOffers] = useState(0);
    const [doneOffers, setDoneOffers] = useState(0);
    const [abortedOffers, setAbortedOffers] = useState(0);

    useEffect(() => {
        if (user === undefined || !user.status) {
            navigation.routes.home.to();
        }
    }, [user]);

    useEffect(() => {
        // Fetch stats data
        const fetchStats = async () => {
            // Fetch new job offers
            getJobOffers(
                0,
                100,
                states.map(s => jobOfferStatusToString(s)),
                "",
                [],
                [],
                0,
                0,
                (data) => {
                    const createdOffersCount = data.content.filter(offer => offer.status === "CREATED").length;
                    setNewJobOffers(createdOffersCount);
                    setSelectionPhaseOffers(data.content.filter(offer => offer.status === "SELECTION_PHASE").length);
                    setCandidateProposalOffers(data.content.filter(offer => offer.status === "CANDIDATE_PROPOSAL").length);
                    setConsolidatedOffers(data.content.filter(offer => offer.status === "CONSOLIDATED").length);
                    setDoneOffers(data.content.filter(offer => offer.status === "DONE").length);
                    setAbortedOffers(data.content.filter(offer => offer.status === "ABORTED").length);
                },
                (error) => setError(error),
                () => setIsLoading(false)
            )
        }

        fetchStats();
    }, []);

    return (
        <Container>
            <Row className="m-4 text-center"><h3>Job Hopper platform at a glance</h3></Row>
            <Row>
                <Col md={6}>
                    <Row>
                        <Col className="dash-box">
                            {/* New job offers */}
                            <Row className="mt-3 text-center"><h5>New job offers: </h5></Row>
                            <Row className="mt-3 text-center"><h5>{newJobOffers}</h5> </Row>
                        </Col>
                        <Col className="dash-box">
                            {/* Job offers in selection phase */}
                            <Row className="mt-3 text-center"><h5>Job offers in selection phase: </h5></Row>
                            <Row className="mt-3 text-center"><h5>{selectionPhaseOffers}</h5></Row>
                        </Col>
                        <Col className="dash-box">
                            {/* Job offers in candidate proposal */}
                            <Row className="mt-3 text-center"><h5>Job offers in candidate proposal: </h5></Row>
                            <Row className="mt-3 text-center"><h5>{candidateProposalOffers}</h5></Row>
                        </Col>
                    </Row>
                    <Row>
                        <Col className="dash-box">
                            {/* Job offers in consolidated phase */}
                            <Row className="mt-3 text-center"><h5>Consolidated job offers: </h5></Row>
                            <Row className="mt-3 text-center"><h5>{consolidatedOffers}</h5></Row>
                        </Col>
                        <Col className="dash-box">
                            {/* Done job offers */}
                            <Row className="mt-3 text-center"><h5>Done job offers: </h5></Row>
                            <Row className="mt-3 text-center"><h5>{doneOffers}</h5></Row>
                        </Col>
                        <Col className="dash-box">
                            {/* Aborted job offers */}
                            <Row className="mt-3 text-center"><h5>Aborted job offers: </h5></Row>
                            <Row className="mt-3 text-center"><h5>{abortedOffers}</h5></Row>
                        </Col>
                    </Row>
                    <Row className="mt-3">
                        <Col xs={12} md={12}>
                            {/* New job offers (last 5) */}
                            <Row className="mt-3 text-center"><h3>New job offers</h3></Row>
                            <NewJobOffersWidget />
                        </Col>
                    </Row>
                </Col>
                <Col md={6}>
                    <Row className="mt-3">
                        <Col>
                            {/* KPIs */}
                            <Row className="mt-3 text-center"><h3>KPIs</h3></Row>

                            <Row className="mt-3 text-center">
                                <Col><h5>Total number of job offers:</h5></Col>
                                <Col><h5>{newJobOffers + selectionPhaseOffers + candidateProposalOffers + consolidatedOffers + doneOffers + abortedOffers}</h5></Col>
                            </Row>

                            <Row className="mt-3 text-center">
                                <Col><h5>Total number of job offers in progress:</h5></Col>
                                <Col><h5>{selectionPhaseOffers + candidateProposalOffers + consolidatedOffers}</h5></Col>
                            </Row>

                            <Row className="mt-3 text-center">
                                <Col><h5>Percentage of done job offers over aborted:</h5></Col>
                                <Col><h5>{(doneOffers / (doneOffers + abortedOffers) * 100).toFixed(2)}%</h5></Col>
                            </Row>

                            <Row className="mt-3 text-center">
                                <Col><h5>Percentage of job offers in progress over total:</h5></Col>
                                <Col><h5>{((selectionPhaseOffers + candidateProposalOffers + consolidatedOffers) / (newJobOffers + selectionPhaseOffers + candidateProposalOffers + consolidatedOffers + doneOffers + abortedOffers) * 100).toFixed(2)}%</h5></Col>
                            </Row>

                            <Row className="mt-3 text-center">
                                <Col><h5>Percentage of job offers in selection phase over total:</h5></Col>
                                <Col><h5>{(selectionPhaseOffers / (newJobOffers + selectionPhaseOffers + candidateProposalOffers + consolidatedOffers + doneOffers + abortedOffers) * 100).toFixed(2)}%</h5></Col>
                            </Row>

                            <Row className="mt-3 text-center">
                                <Col><h5>Percentage of job offers in candidate proposal over total:</h5></Col>
                                <Col><h5>{(candidateProposalOffers / (newJobOffers + selectionPhaseOffers + candidateProposalOffers + consolidatedOffers + doneOffers + abortedOffers) * 100).toFixed(2)}%</h5></Col>
                            </Row>

                            <Row className="mt-3 text-center">
                                <Col><h5>Percentage of job offers in consolidated phase over total:</h5></Col>
                                <Col><h5>{(consolidatedOffers / (newJobOffers + selectionPhaseOffers + candidateProposalOffers + consolidatedOffers + doneOffers + abortedOffers) * 100).toFixed(2)}%</h5></Col>
                            </Row>

                            <Row className="mt-3 text-center">
                                <Col><h5>Percentage of job offers in done phase over total:</h5></Col>
                                <Col><h5>{(doneOffers / (newJobOffers + selectionPhaseOffers + candidateProposalOffers + consolidatedOffers + doneOffers + abortedOffers) * 100).toFixed(2)}%</h5></Col>
                            </Row>

                            <Row className="mt-3 text-center">
                                <Col><h5>Percentage of job offers in aborted phase over total:</h5></Col>
                                <Col><h5>{(abortedOffers / (newJobOffers + selectionPhaseOffers + candidateProposalOffers + consolidatedOffers + doneOffers + abortedOffers) * 100).toFixed(2)}%</h5></Col>
                            </Row>
                        </Col>
                    </Row>
                </Col>
            </Row>
        </Container>
    );
}

export default ManagerPage;