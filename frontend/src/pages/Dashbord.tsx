import {useContext, useEffect, useState} from 'react';
import {Button, Col, Container, Row} from "react-bootstrap";
import {AuthContext, NavigationContext} from "../Context.tsx";
import NewMessagesWidget from "../components/NewMessagesWidget.tsx";
import NewJobOffersWidget from "../components/NewJobOffersWidget.tsx";
import {getJobOffers} from "../api/crm.ts";
import {jobOfferStatusToString, JobOfferStatus} from "../types/JobOffer.ts";
import {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";


function Dashbord() {
    const [error, setError] = useState<ErrorRFC7807 | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const {user} = useContext(AuthContext);
    const navigation = useContext(NavigationContext);
    const [states, setStates] = useState<JobOfferStatus[]>([
        JobOfferStatus.CREATED,
        JobOfferStatus.SELECTION_PHASE,
        JobOfferStatus.CANDIDATE_PROPOSAL,
        JobOfferStatus.CONSOLIDATED
    ]);
    const [newJobOffers, setNewJobOffers] = useState(0);
    const [selectionPhaseOffers, setSelectionPhaseOffers] = useState(0);
    const [candidateProposalOffers, setCandidateProposalOffers] = useState(0);
    const [consolidatedOffers, setConsolidatedOffers] = useState(0);

    useEffect(() => {
        if (user === undefined || !user.status) {
            navigation.routes.home.to();
        }
        if (user && user.roles.includes("manager")) {
            navigation.routes.manager.to();
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
                },
                (error) => setError(error),
                () => setIsLoading(false)
            )
        }

        fetchStats();
    }, []);


    return (
        <Container>
            <Row className="m-4 text-center"><h3>Job Offers at a glance</h3></Row>
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
                <Col className="dash-box">
                    {/* Job offers in consolidated phase */}
                    <Row className="mt-3 text-center"><h5>Consolidated job offers: </h5></Row>
                    <Row className="mt-3 text-center"><h5>{consolidatedOffers}</h5></Row> 
                </Col>
            </Row>
            <Row className="mt-3">
                <Col xs={12} md={5}>
                    {/* New messages (last 5) */}
                    <Row className="mt-3 text-center"><h3>New messages</h3></Row>
                    <NewMessagesWidget/>
                </Col>
                <Col xs={12} md={2}>
                    {/* New contact button */}
                    <Row className="mt-3"><Button onClick={() => navigation.routes.newContact.to()}>New Contact</Button></Row>
                </Col>
                <Col xs={12} md={5}>
                    {/* New job offers (last 5) */}
                    <Row className="mt-3 text-center"><h3>New job offers</h3></Row>
                    <NewJobOffersWidget/>
                </Col>
            </Row>
        </Container>
    );
}

export default Dashbord;