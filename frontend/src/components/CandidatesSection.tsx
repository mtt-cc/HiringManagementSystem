import {Candidate, JobOfferStatus} from "../types/JobOffer.ts";
import {
    Badge,
    Button,
    Card,
    Col,
    Container,
    Form,
    InputGroup,
    ListGroup,
    Offcanvas,
    OverlayTrigger,
    Row,
    Stack,
    Tooltip
} from "react-bootstrap";
import {IoOpenOutline} from "react-icons/io5";
import OverableBadge from "./OverableBadge.tsx";
import BadgeList from "./BadgeList.tsx";
import {useEffect, useState} from "react";
import {
    CiCircleCheck,
    CiCirclePlus,
    CiSaveUp2,
    CiSearch,
    CiSquareCheck,
    CiSquareRemove,
    CiStickyNote
} from "react-icons/ci";
import {Formik} from "formik";
import BasicTextArea from "./form/BasicTextArea.tsx";
import Spacer from "./Spacer.tsx";
import {Skill} from "../types/Skill.ts";
import {CgUnavailable} from "react-icons/cg";
import {MdClear} from "react-icons/md";
import {EmploymentState, parseEmploymentState, ProfessionalType} from "../types/Contact.ts";
import {getProfessionals} from "../api/crm.ts";
import PageBar from "./PageBar.tsx";
import {Pageable} from "../types/Pageable.ts";
import {GrValidate} from "react-icons/gr";
import {highlightText} from "../utils.tsx";

interface CandidatesSectionProps {
    candidates: Candidate[];
    requiredSkills: Skill[];
    professional: ProfessionalType | null;
    currentStatus: JobOfferStatus;
    onSave: (candidate: Candidate) => Promise<void>;
    goToProfessional: (id: number) => void;
    onCandidateSelect: (candidate: Candidate | null) => void;
    selectedCandidate: Candidate | null;
    candidate: (id: number) => void;
    jobOfferId: number;
}

function CandidatesSection(props: CandidatesSectionProps) {
    switch (props.currentStatus) {
        case JobOfferStatus.CREATED: {
            return null;
        }
        case JobOfferStatus.SELECTION_PHASE: {
            return (
                <SelectionPhaseCandidatesSection
                    candidates={props.candidates}
                    requiredSkills={props.requiredSkills}
                    onSave={props.onSave}
                    goToProfessional={props.goToProfessional}
                    onCandidateSelect={props.onCandidateSelect}
                    selectedCandidate={props.selectedCandidate}
                    candidate={props.candidate}
                    jobOfferId={props.jobOfferId}
                />
            )
        }
        case JobOfferStatus.CANDIDATE_PROPOSAL: {
            return (
                <ProfessionalSection
                    professional={props.professional!!}
                    label={"PENDING"}
                    tooltip={"The candidate was selected but it is still not confirmed by the customer"}
                    goToProfessional={props.goToProfessional}
                />
            )
        }
        case JobOfferStatus.CONSOLIDATED: {
            return (
                <ProfessionalSection
                    professional={props.professional!!}
                    label={"READY TO WORK"}
                    tooltip={"The candidate was confirmed by the customer and it is ready to work"}
                    goToProfessional={props.goToProfessional}
                />
            )
        }
        case JobOfferStatus.DONE: {
            return (
                <ProfessionalSection
                    professional={props.professional!!}
                    label={"WORK COMPLETED"}
                    tooltip={"The professional has completed the work"}
                    goToProfessional={props.goToProfessional}
                />
            )
        }
        case JobOfferStatus.ABORTED: {
            if (props.professional !== null) {
                return (<ProfessionalSection
                    professional={props.professional}
                    label={"ABORTED"}
                    tooltip={"The job offer was cancelled"}
                    goToProfessional={null}
                />);
            } else {
                return (<AbortedWithoutProfessionalSection/>);
            }
        }
    }
}

function SelectionPhaseCandidatesSection(props: {
    candidates: Candidate[];
    requiredSkills: Skill[];
    onSave: (candidate: Candidate) => Promise<void>;
    goToProfessional: (id: number) => void;
    onCandidateSelect: (candidate: Candidate | null) => void;
    candidate: (id: number) => void;
    selectedCandidate: Candidate | null;
    jobOfferId: number;
}) {
    const [skillsSet, setSkillsSet] = useState(props.requiredSkills.map((s: Skill) => ({s, selected: false})));
    const [available, setAvailable] = useState(true);
    const [candidateOnly, setCandidateOnly] = useState(false);
    const [suggestedProfessionals, setSuggestedProfessionals] = useState<Pageable<ProfessionalType> | undefined>();
    const [page, setPage] = useState(0);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        if (!candidateOnly) {
            getProfessionals(
                page,
                10,
                skillsSet.filter((s) => s.selected).map((s) => s.s),
                props.jobOfferId,
                available,
                data => setSuggestedProfessionals(data),
                (e) => {
                    setSuggestedProfessionals(undefined)
                    console.error(e)
                },
                () => {
                    setLoading(false)
                }
            )
        }
    }, [candidateOnly, page, skillsSet, available]);

    const [search, setSearch] = useState("");

    const evaluateCandidate = (candidate: Candidate) => {
        if (available && !(parseEmploymentState(candidate.candidate.related_professional.employment_state) === EmploymentState.UNEMPLOYED)) return false;

        for (let skill of skillsSet.filter((s) => s.selected)) {
            if (!candidate.candidate.related_professional.skills.includes(skill.s)) {
                return false;
            }
        }

        return (candidate.candidate.first_name + " " + candidate.candidate.last_name).toLowerCase().includes(search.toLowerCase()) || candidate.notes.toLowerCase().includes(search.toLowerCase());
    }

    const evaluateProfessional = (candidate: ProfessionalType) => {
        if (available && !(parseEmploymentState(candidate.related_professional.employment_state) === EmploymentState.UNEMPLOYED)) return false;

        for (let skill of skillsSet.filter((s) => s.selected)) {
            if (!candidate.related_professional.skills.includes(skill.s)) {
                return false;
            }
        }

        return (candidate.first_name + " " + candidate.last_name).toLowerCase().includes(search.toLowerCase());
    }

    return (
        <Card className="p-4">
            <Card.Title>
                Candidates
            </Card.Title>
            <Card.Body>
                <Stack direction="horizontal" gap={3}>
                    <FilterBadge
                        onClick={() => {
                            setAvailable(v => !v)
                            setPage(0)
                        }}
                        value={available}
                        label="Available only"
                        color="success"
                    />
                    <FilterBadge
                        onClick={() => {
                            setCandidateOnly(v => !v)
                            setPage(0)
                        }}
                        value={candidateOnly}
                        label="Candidate only"
                        color="success"
                    />
                    {skillsSet.map((skill) => (
                        <FilterBadge
                            onClick={() => {
                                const updatedSkills = skillsSet.map((s) => {
                                    if (s === skill) {
                                        return {...s, selected: !s.selected}
                                    }
                                    return s;
                                })
                                setSkillsSet(updatedSkills)
                                setPage(0)
                            }}
                            value={skill.selected}
                            label={skill.s}
                            color="primary"
                        />
                    ))}
                </Stack>
                <Spacer height={"1rem"}/>
                <InputGroup>
                    <InputGroup.Text>
                        <CiSearch size={"1.8rem"} color="gray"/>
                    </InputGroup.Text>
                    <Form.Control
                        type="text"
                        placeholder="Search"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />
                    {
                        search.length > 0 ?
                            <InputGroup.Text onClick={() => setSearch("")} style={{cursor: "pointer"}}>
                                <MdClear size={"1.8rem"} color="gray"/>
                            </InputGroup.Text> : null
                    }
                </InputGroup>
                <Spacer height={"1rem"}/>
                <Card.Text className="fw-bold text-center mt-3">Candidates</Card.Text>
                <ListGroup>
                    {props.candidates.filter(evaluateCandidate).length === 0 ?
                        <p className="ps-5">No candidates found</p>
                        :
                        props.candidates.filter(evaluateCandidate).map((candidate) => (
                            <CandidateEntry
                                candidate={candidate}
                                onSave={props.onSave}
                                goToProfessional={props.goToProfessional}
                                onCandidateSelect={props.onCandidateSelect}
                                selected={candidate.candidate.id === props.selectedCandidate?.candidate.id}
                                searchKey={search}
                            />
                        ))}
                </ListGroup>
                {
                    candidateOnly ? null :
                        <>
                            <Card.Text className="fw-bold text-center mt-5">Suggested professionals</Card.Text>
                            <ListGroup>
                                {
                                    loading ? <p>Loading...</p> :
                                        <>
                                            {suggestedProfessionals?.content.filter(evaluateProfessional).length === 0 ?
                                                <p className="ps-5">No professionals found</p>
                                                :
                                                suggestedProfessionals?.content.filter(evaluateProfessional).map((professional) => (
                                                <SuggestedEntry
                                                    professional={professional}
                                                    candidate={props.candidate}
                                                    goToProfessional={props.goToProfessional}
                                                    searchKey={search}
                                                />
                                            ))}
                                            {
                                                suggestedProfessionals?.totalPages!! > 1 ?
                                                    <PageBar
                                                        pageNumber={page}
                                                        totalPages={suggestedProfessionals!!.totalPages}
                                                        setPage={setPage}
                                                    /> : null
                                            }
                                        </>
                                }
                            </ListGroup>
                        </>
                }
            </Card.Body>
        </Card>
    )
}

function FilterBadge(props: {
    onClick: () => void;
    value: boolean;
    label: string;
    color: string;
}) {
    return (
        <Badge bg={props.color} onClick={props.onClick} style={{cursor: "pointer", opacity: props.value ? 1 : .7}}
               className="ps-3 pe-3 pt-2 pb-2">
            {props.label}
        </Badge>
    )
}

function SuggestedEntry(props: {
    professional: ProfessionalType;
    goToProfessional: (id: number) => void;
    candidate: (id: number) => void;
    searchKey: string;
}) {
    return (
        <ListGroup.Item>
            <Row className="">
                <Col className="col-3 d-flex">
                    <p className="fw-bold m-0 align-self-center">{highlightText(`${props.professional.first_name} ${props.professional.last_name}`, props.searchKey)}</p>
                    <IoOpenOutline
                        size={"1.5rem"}
                        onClick={() => props.goToProfessional(props.professional.id)}
                        className="ms-2 pb-1 align-self-center"
                        style={{
                            cursor: "pointer",
                        }}
                    />
                </Col>
                <Col className="col-7 d-flex align-content-center">
                    <BadgeList badges={props.professional.related_professional.skills.map((s) => {
                        return {
                            key: s,
                            text: s,
                        }
                    })}/>
                </Col>
                <Col className="col-2 d-flex align-self-center justify-content-end">
                    {
                        (parseEmploymentState(props.professional.related_professional.employment_state) === EmploymentState.UNEMPLOYED) ? null :
                            <OverableBadge
                                content={<CgUnavailable size="1.8rem"/>}
                                tooltip={props.professional.related_professional.employment_state}
                                bg={"danger"}
                                className={"p-1 me-2"}
                                pill
                            />
                    }
                    <Button variant="outline-primary" style={{padding: ".2rem"}} className="ms-2"
                            onClick={() => props.candidate(props.professional.id)}>
                        <GrValidate size="1.8rem"/>
                    </Button>
                </Col>
            </Row>
        </ListGroup.Item>
    )
}

function CandidateEntry(props: {
    candidate: Candidate;
    onSave: (candidate: Candidate) => Promise<void>;
    goToProfessional: (id: number) => void;
    onCandidateSelect: (candidate: Candidate | null) => void;
    selected: boolean;
    searchKey: string;
}) {
    const [showOffcanvas, setShowOffcanvas] = useState(false);

    return (
        <ListGroup.Item>
            <Row className="">
                <Col className="col-3 d-flex">
                    <p className="fw-bold m-0 align-self-center">{highlightText(`${props.candidate.candidate.first_name} ${props.candidate.candidate.last_name}`, props.searchKey)}</p>
                    <IoOpenOutline
                        size={"1.5rem"}
                        onClick={() => props.goToProfessional(props.candidate.candidate.id)}
                        className="ms-2 pb-1 align-self-center"
                        style={{
                            cursor: "pointer",
                        }}
                    />
                </Col>
                <Col className="col-7 d-flex align-content-center">
                    <BadgeList badges={props.candidate.candidate.related_professional.skills.map((s) => {
                        return {
                            key: s,
                            text: s,
                        }
                    })}/>
                </Col>
                <Col className="col-2 d-flex align-self-center justify-content-end">
                    {
                        (parseEmploymentState(props.candidate.candidate.related_professional.employment_state) === EmploymentState.UNEMPLOYED) ? null :
                            <OverableBadge
                                content={<CgUnavailable size="1.8rem"/>}
                                tooltip={props.candidate.candidate.related_professional.employment_state}
                                bg={"danger"}
                                className={"p-1 me-2"}
                                pill
                            />
                    }
                    <OverableBadge
                        content={props.candidate.verified ? <CiSquareCheck size="1.8rem"/> :
                            <CiSquareRemove size="1.8rem"/>}
                        tooltip={props.candidate.verified ? "Already interviewed" : "Not yet interviewed"}
                        bg={props.candidate.verified ? "success" : "danger"}
                        className={"p-1"}
                    />
                    <Button variant="outline-secondary" style={{padding: ".2rem"}} className="ms-2"
                            onClick={() => setShowOffcanvas(true)}>
                        <CiStickyNote size="1.8rem"/>
                    </Button>
                    {
                        props.selected ?
                            <Button variant="success" style={{padding: ".2rem"}} className="ms-2"
                                    onClick={() => props.onCandidateSelect(null)}>
                                <CiCircleCheck size="1.8rem"/>
                            </Button> :
                            <Button variant="outline-success" style={{padding: ".2rem"}} className="ms-2"
                                    onClick={() => props.onCandidateSelect(props.candidate)}>
                                <CiCirclePlus size="1.8rem"/>
                            </Button>
                    }
                </Col>
            </Row>
            <CandidateNotesOffcanvas
                show={showOffcanvas}
                onHide={() => setShowOffcanvas(false)}
                onSave={async (notes) => {
                    const updatedCandidate = {...props.candidate, notes: notes}
                    await props.onSave(updatedCandidate)
                }}
                verify={async () => {
                    const updatedCandidate = {...props.candidate, verified: true}
                    await props.onSave(updatedCandidate)
                }}
                candidate={props.candidate}
            />
        </ListGroup.Item>
    )
}

function CandidateNotesOffcanvas(props: {
    show: boolean;
    onHide: () => void;
    onSave: (notes: string) => Promise<void>;
    verify: () => Promise<void>;
    candidate: Candidate;
}) {
    const showTooltip = (props: any) => (
        <Tooltip id="button-tooltip" {...props}>
            Already interviewed
        </Tooltip>
    );

    return (
        <Offcanvas
            show={props.show}
            onHide={props.onHide}
            placement="end"
        >
            <Offcanvas.Header closeButton>
                <Offcanvas.Title>Candidate notes</Offcanvas.Title>
            </Offcanvas.Header>
            <Offcanvas.Body>
                <div className="d-flex align-content-center mb-4">
                    <p className="align-self-center m-0">{props.candidate.candidate.first_name} {props.candidate.candidate.last_name}</p>
                    {
                        props.candidate.verified ?
                            <OverlayTrigger overlay={showTooltip} placement="top-start">
                                <div
                                    className="bg-success text-white d-flex ms-2"
                                    style={{borderRadius: "50%", width: "2rem", height: "2rem"}}
                                >
                                    <CiCircleCheck size={"2rem"}/>
                                </div>
                            </OverlayTrigger>
                            : null}</div>
                <Formik
                    initialValues={{
                        notes: props.candidate.notes
                    }}
                    onSubmit={(values, onSubmitProps) => {
                        props.onSave(values.notes).then(() => {
                            onSubmitProps.setSubmitting(false)
                        })
                    }}
                    validationSchema={null}
                >
                    {
                        (formik) => (
                            <Form onSubmit={formik.handleSubmit} noValidate>
                                <BasicTextArea
                                    name={"notes"}
                                    label={"Notes"}
                                    value={formik.values.notes}
                                    touched={formik.touched.notes}
                                    error={formik.errors.notes}
                                    onChange={formik.handleChange}
                                    onBlur={formik.handleBlur}
                                    disabled={formik.isSubmitting}
                                />
                                <Spacer height={"1rem"} borderBottom={".1rem solid lightgray"}/>
                                <Container fluid className="d-flex justify-content-end">
                                    {
                                        props.candidate.verified ? null :
                                            <Button variant={"outline-success"} className="me-2" onClick={() => {
                                                formik.setSubmitting(true)
                                                props.verify().then(() => {
                                                    formik.setSubmitting(false)
                                                })
                                            }} disabled={formik.isSubmitting}>
                                                <span className="d-flex align-content-center">Interviewed <CiCircleCheck
                                                    className="ms-2" size="1.5rem"/></span>
                                            </Button>
                                    }
                                    <Button type="submit" disabled={formik.isSubmitting}>
                                        <span className="d-flex align-content-center">Save <CiSaveUp2 className="ms-2"
                                                                                                      size="1.5rem"/></span>
                                    </Button>
                                </Container>
                            </Form>
                        )
                    }
                </Formik>
            </Offcanvas.Body>
        </Offcanvas>
    )
}

function ProfessionalSection(props: {
    professional: ProfessionalType;
    label: string;
    tooltip: string;
    goToProfessional: ((id: number) => void) | null;
}) {
    return (
        <Card>
            <Card.Body>
                <Card.Text>
                    <span className="fw-bold ms-5">{props.professional.first_name} {props.professional.last_name}</span>
                    {
                        props.goToProfessional === null ? null :
                            <IoOpenOutline
                                size={"1.8rem"}
                                onClick={() => {
                                    if (props.goToProfessional !== null) props.goToProfessional(props.professional.id)
                                }}
                                className="ms-2 pb-1"
                                style={{
                                    cursor: "pointer",
                                }}
                            />
                    }
                    <OverableBadge
                        content={props.label}
                        tooltip={props.tooltip}
                        className="ps-4 pe-4 pt-2 pb-2"
                        style={{position: "absolute", right: "3rem"}}
                        bg={props.label === "ABORTED" ? "danger" : "primary"}
                    />
                </Card.Text>
            </Card.Body>
        </Card>
    )
}

function AbortedWithoutProfessionalSection() {
    return (
        <Card>
            <Card.Body>
                <Card.Text className="d-flex justify-content-center">
                    <Badge bg="danger" className="ps-3 pe-3 pt-2 pb-2">
                        Job offer aborted before candidate selection
                    </Badge>
                </Card.Text>
            </Card.Body>
        </Card>
    )
}

export default CandidatesSection;