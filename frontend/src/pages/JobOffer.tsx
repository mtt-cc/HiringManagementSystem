import {Button, Card, Col, Container, Row, Spinner} from "react-bootstrap";
import {
    Candidate,
    JobOffer as JobOfferType,
    JobOfferStatus, jobOfferStatusToString,
    mapHistoryToStateBarEntry,
    stringToJobOfferStatus
} from "../types/JobOffer.ts";
import {IoOpenOutline} from "react-icons/io5";
import ProgressStateBar from "../components/ProgressStateBar.tsx";
import EditableBadgeList from "../components/EditableBadgeList.tsx";
import Spacer from "../components/Spacer.tsx";
import * as Yup from "yup";
import EditableParagraph from "../components/EditableParagraph.tsx";
import BadgeList from "../components/BadgeList.tsx";
import CandidatesSection from "../components/CandidatesSection.tsx";
import {Skill} from "../types/Skill.ts";
import JobOfferHistory from "../components/JobOfferHistory.tsx";
import {useContext, useEffect, useState} from "react";
import JobOfferContextBar from "../components/JobOfferContextBar.tsx";
import JobOfferNotes from "../components/JobOfferNotes.tsx";
import ErrorModalRFC7807, {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";
import {useParams} from "react-router-dom";
import {AuthContext, NavigationContext} from "../Context.tsx";
import {
    candidateForJobOffer,
    getJobOffer, updateCandidate,
    updateJobOfferDescription,
    updateJobOfferNotes,
    updateJobOfferSkills,
    updateJobOfferState
} from "../api/crm.ts";

function JobOffer() {
    const {id} = useParams();

    const navigation = useContext(NavigationContext);
    const {user} = useContext(AuthContext);

    const [error, setError] = useState<ErrorRFC7807 | null>(null);

    const [jobOffer, setJobOffer] = useState<JobOfferType | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [reload, setReload] = useState(false);

    useEffect(() => {
        if (user === undefined || !user.status) {
            navigation.routes.home.to();
        }
    }, [user]);

    useEffect(() => {
        setIsLoading(true);
        if (id !== undefined) {
            getJobOffer(
                id,
                (data) => {
                    setJobOffer(
                        {
                            ...data,
                            status: stringToJobOfferStatus(data.status),
                            history: data.history.map((entry) => {
                                return {
                                    ...entry,
                                    current_status: stringToJobOfferStatus(entry.current_state),
                                    previous_status: entry.previous_state !== null ? stringToJobOfferStatus(entry.previous_state) : null,
                                }
                            })
                        });
                },
                (error) => {
                    setError(error);
                },
                () => {
                    setIsLoading(false);
                }
            )
        }
    }, [reload]);

    const addSkill = async (s: Skill) => {
        updateJobOfferSkills(
            jobOffer!!.id.toString(),
            jobOffer!!.skills.concat(s),
            user?.xsrfToken,
            () => {setReload(!reload)},
            (e) => {setError(e)},
            () => {}
        )
    }

    const removeSkill = async (s: Skill) => {
        updateJobOfferSkills(
            jobOffer!!.id.toString(),
            jobOffer!!.skills.filter((skill) => skill !== s),
            user?.xsrfToken,
            () => {setReload(!reload)},
            (e) => {setError(e)},
            () => {}
        )
    }

    const editDescription = async (d: string) => {
        updateJobOfferDescription(
            jobOffer!!.id.toString(),
            d,
            user?.xsrfToken,
            () => {setReload(!reload)},
            (e) => {setError(e)},
            () => {}
        )
    }

    const editJobOfferNotes = async (notes: string) => {
        updateJobOfferNotes(
            jobOffer!!.id.toString(),
            notes,
            user?.xsrfToken,
            () => {setReload(!reload)},
            (e) => {setError(e)},
            () => {}
        )
    }

    const goToCustomer = (id: number) => {
        navigation.routes.contact.to(id);
    }

    const goToProfessional = (id: number) => {
        navigation.routes.contact.to(id);
    }

    const saveCandidate = async (candidate: Candidate) => {
        updateCandidate(
            candidate.id.toString(),
            candidate.notes,
            candidate.verified,
            user?.xsrfToken,
            () => {setReload(!reload)},
            (e) => {setError(e)},
            () => {}
        )
    }

    const candidate = async (id: number)=> {
        candidateForJobOffer(
            jobOffer!!.id,
            id,
            user?.xsrfToken!!,
            () => {setReload(!reload)},
            (e) => {setError(e)},
            () => {}
        )
    }

    const handleStateChangeRequest = async (newState: JobOfferStatus) => {
        if (newState === JobOfferStatus.CANDIDATE_PROPOSAL && selectedCandidate === null) {
            setError({
                title: "No candidate selected",
                instance: "frontend",
                detail: "Please select a candidate before changing the state to CANDIDATE_PROPOSAL",
            })
            return;
        }

        updateJobOfferState(
            jobOffer!!.id.toString(),
            jobOfferStatusToString(newState).toLowerCase().replace(" ", "_"),
            selectedCandidate !== null ? selectedCandidate.candidate.id : null,
            user?.xsrfToken,
            () => {
                setSelectedCandidate(null);
                setReload(!reload)
            },
            (e) => {setError(e)},
            () => {}
        )
    }

    const [showHistory, setShowHistory] = useState(false);
    const [showJobOfferNotes, setShowJobOfferNotes] = useState(false);
    const [selectedCandidate, setSelectedCandidate] = useState<Candidate | null>(null);

    if (isLoading) {
        return (
            <Container className="mt-3 d-flex justify-content-center">
                <Card className="mt-5" style={{width: "20rem"}}>
                    <Card.Body className="d-flex justify-content-center">
                        <h4 className="me-2 mb-0">Loading...</h4> <Spinner variant="primary" animation="border"/>
                    </Card.Body>
                </Card>
            </Container>
        );
    }

    if (jobOffer === null) {
        return (
            <Container className="mt-3">
                {error !== null && <ErrorModalRFC7807 error={error} onHide={() => setError(null)} show={true}/>}
            </Container>
        );
    }

    return (
        <Container className="mt-3">
            {error !== null && <ErrorModalRFC7807 error={error} onHide={() => setError(null)} show={true}/>}
            <Card>
                <Row>
                    <Col className="col-2">
                        <Button variant="outline-primary mt-3 ms-3" style={{width: "7.5rem"}} onClick={() => {
                            navigation.navigate!!(-1)
                        }}>
                            Go back
                        </Button>
                    </Col>
                    <Col className="col-8">
                        <Card.Title className="w-100 text-center mt-3" style={{fontSize: "1.5rem"}}>
                            {jobOffer!!.title}
                        </Card.Title>
                    </Col>
                    <Col className="col-2">
                    </Col>
                </Row>
                <Card.Subtitle className="d-flex ms-5 mt-3" style={{fontSize: "1.2rem"}}>
                    <Col className="f-flex align-content-center">
                        {jobOffer!!.customer.first_name} {jobOffer!!.customer.last_name}
                        <IoOpenOutline
                            size={"1.8rem"}
                            onClick={() => goToCustomer(jobOffer!!.customer.id)}
                            className="ms-2 pb-1"
                            style={{
                                cursor: "pointer",
                            }}
                        />
                    </Col>
                    <Col className="d-flex flex-nowrap justify-content-end pe-5">
                        <JobOfferContextBar
                            status={jobOffer!!.status}
                            onStateChangeRequest={handleStateChangeRequest}
                            showNotes={() => setShowJobOfferNotes(true)}
                        />
                        <JobOfferNotes
                            notes={jobOffer!!.notes}
                            show={showJobOfferNotes}
                            onHide={() => setShowJobOfferNotes(false)}
                            onSave={editJobOfferNotes}
                            status={jobOffer!!.status}
                        />
                    </Col>
                </Card.Subtitle>
                <Card.Body>
                    <Spacer height={"1rem"} borderBottom={".1rem solid lightgray"}/>
                    <Row style={{
                        width: "80%",
                        marginLeft: "10%",
                        marginTop: "4rem",
                        marginBottom: "4rem",
                    }}>
                        <ProgressStateBar
                            states={mapHistoryToStateBarEntry(jobOffer!!.history)}
                            currentState={jobOffer!!.status}
                            aborted={jobOffer!!.status === JobOfferStatus.ABORTED}
                        />
                    </Row>
                    <Spacer height={"1rem"}/>
                    <Row style={{
                        width: "80%",
                        marginLeft: "10%",
                    }}>
                        {
                            jobOffer!!.status === JobOfferStatus.CREATED || jobOffer!!.status === JobOfferStatus.SELECTION_PHASE ?
                                <EditableBadgeList
                                    badges={jobOffer!!.skills.map((s) => {
                                        return {
                                            key: s,
                                            text: s,
                                        }
                                    })}
                                    label={"required skills"}
                                    onRemove={(badge) => removeSkill(badge.text)}
                                    onAdd={(badge) => addSkill(badge.text)}
                                />
                                :
                                <BadgeList
                                    badges={jobOffer!!.skills.map((s) => {
                                        return {
                                            key: s,
                                            text: s,
                                        }
                                    })}
                                />

                        }
                    </Row>
                    <Spacer height={"3rem"} borderBottom={".1rem solid lightgray"}/>
                    <Spacer height={"2.5rem"}/>
                    <Row style={{
                        width: "90%",
                        marginLeft: "5%",
                    }}>
                        <Button onClick={() => setShowHistory(true)}>
                            Show complete job offer history
                        </Button>
                        <JobOfferHistory
                            history={jobOffer!!.history}
                            title={jobOffer!!.title}
                            show={showHistory}
                            onHide={() => setShowHistory(false)}
                        />
                    </Row>
                    <Spacer height={"3rem"}/>
                    <Row style={{
                        width: "88%",
                        marginLeft: "6%",
                    }}>
                        <EditableParagraph
                            title="Job offer description"
                            body={jobOffer!!.description}
                            validationSchema={
                                Yup.object({
                                    body: Yup.string(),
                                })
                            }
                            onChange={(body) => editDescription(body)}
                            bodyLabel={"Description"}
                            disabled={jobOffer!!.status !== JobOfferStatus.CREATED && jobOffer!!.status !== JobOfferStatus.SELECTION_PHASE}
                        />
                    </Row>
                    <Spacer height={"3rem"}/>
                    <Row style={{
                        width: "88%",
                        marginLeft: "6%",
                    }}>
                        <CandidatesSection
                            candidates={jobOffer!!.candidates}
                            professional={jobOffer!!.professional}
                            requiredSkills={jobOffer!!.skills}
                            currentStatus={jobOffer!!.status}
                            onSave={saveCandidate}
                            onCandidateSelect={(candidate) => setSelectedCandidate(candidate)}
                            selectedCandidate={selectedCandidate}
                            goToProfessional={goToProfessional}
                            candidate={candidate}
                            jobOfferId={jobOffer!!.id}
                        />
                    </Row>
                    <Spacer height={"3rem"}/>
                </Card.Body>
            </Card>
        </Container>
    );
}

export default JobOffer;