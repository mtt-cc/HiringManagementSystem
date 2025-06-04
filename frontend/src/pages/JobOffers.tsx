import {useContext, useEffect, useState} from 'react';
import {
    Button,
    Card,
    Col,
    Container, Form,
    InputGroup, Modal,
    Offcanvas,
    Row,
    Spinner,
    ToggleButton
} from "react-bootstrap";
import ErrorModalRFC7807, {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";
import {JobOfferHeader, JobOfferStatus, jobOfferStatusToString} from "../types/JobOffer.ts";
import JobOfferCard from "../components/JobOfferCard.tsx";
import Spacer from "../components/Spacer.tsx";
import {GoFilter} from "react-icons/go";
import {AuthContext, NavigationContext} from "../Context.tsx";
import {TfiPlus} from "react-icons/tfi";
import {createJobOffer, getCustomerOption, getJobOffers, getProfessionalOption} from "../api/crm.ts";
import {Pageable} from "../types/Pageable.ts";
import PageBar from "../components/PageBar.tsx";
import BasicTextInput from "../components/form/BasicTextInput.tsx";
import {Typeahead} from "react-bootstrap-typeahead";
import 'react-bootstrap-typeahead/css/Typeahead.css';
import {Option} from "react-bootstrap-typeahead/types/types";
import {ContactHeader} from "../types/Contact.ts";
import {Formik} from "formik";
import * as Yup from "yup";
import BasicTextArea from "../components/form/BasicTextArea.tsx";
import EditableBadgeList from "../components/EditableBadgeList.tsx";
import {Skill} from "../types/Skill.ts";
import {BadgeEntry} from "../components/BadgeList.tsx";
import Select from "react-select";

function JobOffers() {
    const {user} = useContext(AuthContext);

    const [error, setError] = useState<ErrorRFC7807 | null>(null);
    const navigation = useContext(NavigationContext);

    const [isLoading, setIsLoading] = useState(false);

    const [states, setStates] = useState<JobOfferStatus[]>([
        JobOfferStatus.CREATED,
        JobOfferStatus.SELECTION_PHASE,
        JobOfferStatus.CANDIDATE_PROPOSAL,
        JobOfferStatus.CONSOLIDATED
    ]);
    const [search, setSearch] = useState<string>("");
    const [customers, setCustomers] = useState<Option[]>([]);
    const [professionals, setProfessionals] = useState<Option[]>([]);
    const [valueLow, setValueLow] = useState<number>(0);
    const [valueHigh, setValueHigh] = useState<number>(0);

    const [reload, setReload] = useState(true);
    const [showFilters, setShowFilters] = useState(false);
    const [showNewJobOfferModal, setShowNewJobOfferModal] = useState(false);

    const [jobOffers, setJobOffers] = useState<Pageable<JobOfferHeader> | undefined>(undefined);
    const [page, setPage] = useState<number>(0);
    const [size, setSize] = useState<number>(10);

    const [pOptions, setPOptions] = useState<ContactHeader[]>([]);
    const [cOptions, setCOptions] = useState<ContactHeader[]>([]);


    useEffect(() => {
        if (user === undefined || !user.status) {
            navigation.routes.home.to();
        }
    }, [user]);

    useEffect(() => {
        getProfessionalOption(
            (data) => setPOptions(data),
            (error) => setError(error),
            () => {
            }
        );
        getCustomerOption(
            (data) => setCOptions(data),
            (error) => setError(error),
            () => {
            }
        );
    }, []);

    useEffect(() => {
        setIsLoading(true);
        getJobOffers(
            page,
            size,
            states.map(s => jobOfferStatusToString(s)),
            search,
            customers.map(c => c.id),           // ignore this error (due to type mismatch)
            professionals.map(p => p.id),       // ignore this error (due to type mismatch)
            valueLow,
            valueHigh,
            (data) => setJobOffers(data),
            (error) => setError(error),
            () => setIsLoading(false)
        )
    }, [page, size, reload]);

    const onSave = (values: any) => {
        setIsLoading(true);
        createJobOffer(
            {
                budget: values.budget,
                customer_id: values.customer.id,
                description: values.description,
                duration: values.duration,
                notes: "",
                skills: values.skills,
                title: values.title
            },
            user?.xsrfToken!!,
            () => {
                setReload(r => !r);
                setShowNewJobOfferModal(false);
            },
            (error) => setError(error),
            () => {
                setIsLoading(false);
            }
        )
    }

    const goToJobOffer = (id: number) => {
        navigation.routes.jobOffer.to(id);
    }

    const goToCustomer = (id: number) => {
        navigation.routes.contact.to(id);
    }

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

    return (
        <Container className="mt-3">
            {error !== null && <ErrorModalRFC7807 error={error} onHide={() => setError(null)} show={true}/>}
            <Row className="mt-2 mb-2">
                <Col className="col-2 h-100">
                    <InputGroup style={{position: "relative", left: "1rem"}} className="h-100">
                        <InputGroup.Text># Elements</InputGroup.Text>
                        <Form.Select
                            value={size}
                            onChange={(e) => {
                                setSize(parseInt(e.target.value))
                                setPage(0)
                            }}
                        >
                            <option value={10}>10</option>
                            <option value={20}>20</option>
                            <option value={50}>50</option>
                            <option value={100}>100</option>
                        </Form.Select>
                    </InputGroup>
                </Col>
                <Col className="col-10 d-flex justify-content-end">
                    <Button variant="secondary" className="pt-2 w-25 me-3" onClick={() => setShowFilters(true)}>
                        <span className="m-0 me-3">Filters</span> <GoFilter size={"1.5rem"} style={{
                        position: "relative",
                        top: "-.1rem"
                    }}/>
                    </Button>
                    <Button variant="success" className="pt-2 w-25" onClick={() => setShowNewJobOfferModal(true)}>
                        <span className="m-0 me-3">Create new Job Offer</span> <TfiPlus size={"1.5rem"} style={{
                        position: "relative",
                        top: "-.1rem"
                    }}/>
                    </Button>
                </Col>
            </Row>
            <Spacer height={"1.5rem"}/>
            <Card>
                <Card.Body className="w-100">
                    {
                        jobOffers === undefined || jobOffers.content.length === 0 ?
                            <p className="w-100 text-center m-0">No Job offer found</p>
                            :
                            <>
                                {
                                    jobOffers.content.map((jobOffer, index) => (
                                        <JobOfferCard
                                            key={index}
                                            jobOffer={jobOffer}
                                            goToJobOffer={goToJobOffer}
                                            goToCustomer={goToCustomer}
                                            className={index !== 0 ? "mt-4" : ""}
                                        />
                                    ))
                                }
                                <Row>
                                    <Col className="d-flex justify-content-center">
                                        {
                                            jobOffers.totalPages > 1 ?
                                                <PageBar
                                                    pageNumber={jobOffers.pageable.pageNumber}
                                                    totalPages={jobOffers.totalPages}
                                                    setPage={setPage}
                                                />
                                                :
                                                null
                                        }
                                    </Col>
                                </Row>
                            </>
                    }
                </Card.Body>
            </Card>
            <Offcanvas show={showFilters} onHide={() => setShowFilters(false)} placement={"end"}
                       style={{width: "70rem"}}>
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>Filters</Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>
                    <Row className="mt-3">
                        <InputGroup>
                            <ToggleButton
                                id="status-created"
                                value={JobOfferStatus.CREATED}
                                variant="outline-success"
                                type={"checkbox"}
                                name={"states"}
                                checked={states.includes(JobOfferStatus.CREATED)}
                                onChange={(e) => {
                                    if (e.target.checked) {
                                        setStates([...states, JobOfferStatus.CREATED]);
                                    } else {
                                        setStates(states.filter(s => s !== JobOfferStatus.CREATED));
                                    }
                                }}
                                style={{width: "16.6667%", height: "3.5rem", borderRadius: "0.5rem 0 0 0.5rem"}}
                                className="d-flex justify-content-center align-items-center"
                            >
                                {jobOfferStatusToString(JobOfferStatus.CREATED)}
                            </ToggleButton>

                            <ToggleButton
                                id="status-selection-phase"
                                value={JobOfferStatus.SELECTION_PHASE}
                                variant="outline-success"
                                type={"checkbox"}
                                name={"states"}
                                checked={states.includes(JobOfferStatus.SELECTION_PHASE)}
                                onChange={(e) => {
                                    if (e.target.checked) {
                                        setStates([...states, JobOfferStatus.SELECTION_PHASE]);
                                    } else {
                                        setStates(states.filter(s => s !== JobOfferStatus.SELECTION_PHASE));
                                    }
                                }}
                                style={{width: "16.6666%"}}
                                className="d-flex justify-content-center align-items-center"
                            >
                                {jobOfferStatusToString(JobOfferStatus.SELECTION_PHASE)}
                            </ToggleButton>

                            <ToggleButton
                                id="status-candidate-proposal"
                                value={JobOfferStatus.CANDIDATE_PROPOSAL}
                                variant="outline-success"
                                type={"checkbox"}
                                name={"states"}
                                checked={states.includes(JobOfferStatus.CANDIDATE_PROPOSAL)}
                                onChange={(e) => {
                                    if (e.target.checked) {
                                        setStates([...states, JobOfferStatus.CANDIDATE_PROPOSAL]);
                                    } else {
                                        setStates(states.filter(s => s !== JobOfferStatus.CANDIDATE_PROPOSAL));
                                    }
                                }}
                                style={{width: "16.6666%"}}
                                className="d-flex justify-content-center align-items-center"
                            >
                                {jobOfferStatusToString(JobOfferStatus.CANDIDATE_PROPOSAL)}
                            </ToggleButton>

                            <ToggleButton
                                id="status-consolidated"
                                value={JobOfferStatus.CONSOLIDATED}
                                variant="outline-success"
                                type={"checkbox"}
                                name={"states"}
                                checked={states.includes(JobOfferStatus.CONSOLIDATED)}
                                onChange={(e) => {
                                    if (e.target.checked) {
                                        setStates([...states, JobOfferStatus.CONSOLIDATED]);
                                    } else {
                                        setStates(states.filter(s => s !== JobOfferStatus.CONSOLIDATED));
                                    }
                                }}
                                style={{width: "16.6666%"}}
                                className="d-flex justify-content-center align-items-center"
                            >
                                {jobOfferStatusToString(JobOfferStatus.CONSOLIDATED)}
                            </ToggleButton>

                            <ToggleButton
                                id="status-done"
                                value={JobOfferStatus.DONE}
                                variant="outline-success"
                                type={"checkbox"}
                                name={"states"}
                                checked={states.includes(JobOfferStatus.DONE)}
                                onChange={(e) => {
                                    if (e.target.checked) {
                                        setStates([...states, JobOfferStatus.DONE]);
                                    } else {
                                        setStates(states.filter(s => s !== JobOfferStatus.DONE));
                                    }
                                }}
                                style={{width: "16.6666%"}}
                                className="d-flex justify-content-center align-items-center"
                            >
                                {jobOfferStatusToString(JobOfferStatus.DONE)}
                            </ToggleButton>

                            <ToggleButton
                                id="status-aborted"
                                value={JobOfferStatus.ABORTED}
                                variant="outline-success"
                                type={"checkbox"}
                                name={"states"}
                                checked={states.includes(JobOfferStatus.ABORTED)}
                                onChange={(e) => {
                                    if (e.target.checked) {
                                        setStates([...states, JobOfferStatus.ABORTED]);
                                    } else {
                                        setStates(states.filter(s => s !== JobOfferStatus.ABORTED));
                                    }
                                }}
                                style={{width: "16.6666%", borderRadius: "0 0.5rem 0.5rem 0"}}
                                className="d-flex justify-content-center align-items-center"
                            >
                                {jobOfferStatusToString(JobOfferStatus.ABORTED)}
                            </ToggleButton>

                        </InputGroup>
                    </Row>
                    <Row className="mt-3">
                        <BasicTextInput
                            name="search"
                            label="Search"
                            value={search}
                            touched={false}
                            error={undefined}
                            onChange={(e) => setSearch(e.target.value)}
                            onBlur={() => {
                            }}
                        />
                    </Row>
                    <Row className="mt-3">
                        <label>Customers</label>
                        <Typeahead
                            style={{height: "3.5rem"}}
                            id="customers"
                            multiple
                            placeholder="Select customers"
                            options={cOptions}
                            selected={customers}
                            onChange={(selected) => setCustomers(selected)}
                        />
                    </Row>
                    <Row className="mt-3">
                        <label>Professionals</label>
                        <Typeahead
                            style={{height: "3.5rem"}}
                            id="professionals"
                            multiple
                            placeholder="Select professionals"
                            options={pOptions}
                            selected={professionals}
                            onChange={(selected) => setProfessionals(selected)}
                        />
                    </Row>
                    <Row className="mt-3">
                        <Col>
                            <BasicTextInput
                                type="number"
                                name="value-low"
                                label="Min Value"
                                value={valueLow}
                                touched={false}
                                error={undefined} onChange={(e) => setValueLow(parseInt(e.target.value))}
                                onBlur={() => {
                                }}
                            />
                        </Col>
                        <Col>
                            <BasicTextInput
                                type="number"
                                name="value-low"
                                label="Max Value"
                                value={valueLow}
                                touched={false}
                                error={undefined} onChange={(e) => setValueLow(parseInt(e.target.value))}
                                onBlur={() => {
                                }}
                            />
                        </Col>
                    </Row>
                    <Row className="p-3 justify-content-end">
                        <Button
                            className="w-25 me-3"
                            variant="danger"
                            style={{height: "3.5rem"}}
                            onClick={() => {
                                setSearch("");
                                setStates([
                                    JobOfferStatus.CREATED,
                                    JobOfferStatus.SELECTION_PHASE,
                                    JobOfferStatus.CANDIDATE_PROPOSAL,
                                    JobOfferStatus.CONSOLIDATED
                                ]);
                                setCustomers([]);
                                setProfessionals([]);
                                setValueLow(0);
                                setValueHigh(0);
                            }}
                        >Reset</Button>

                        <Button
                            className="w-25"
                            variant="success"
                            style={{height: "3.5rem"}}
                            onClick={() => {
                                setReload(r => !r);
                                setShowFilters(false);
                            }}
                        >Apply</Button>
                    </Row>
                </Offcanvas.Body>
            </Offcanvas>
            <NewJobOfferModal
                show={showNewJobOfferModal}
                cOptions={cOptions}
                onHide={() => setShowNewJobOfferModal(false)}
                onSave={onSave}
            />
        </Container>
    );
}

function NewJobOfferModal(
    props: {
        show: boolean,
        cOptions: ContactHeader[],
        onHide: () => void,
        onSave: (values : {
            title: string,
            description: string,
            customer: ContactHeader,
            skills: Skill[],
            duration: number,
            budget: number,
        }) => void,
    }
) {
    const initialValues = {
        title: "",
        description: "",
        customer: undefined,
        skills: [] as Skill[],
        duration: 0,
        budget: 0,
    }

    const validationSchema = Yup.object({
        title: Yup.string().required("Title is required"),
        description: Yup.string(),
        customer: Yup.object({
            id: Yup.number().required("Customer is required"),
            first_name: Yup.string().required("Customer is required"),
            last_name: Yup.string().required("Customer is required"),
            category: Yup.string().required("Customer is required"),
        }).required("Customer is required"),
        skills: Yup.array().of(Yup.string().required("Skill is required")),
        duration: Yup.number().min(1).required("Duration is required"),
        budget: Yup.number().min(0).required("Budget is required"),
    })

    return (
        <Modal
            onHide={props.onHide}
            show={props.show}
            centered
            size="xl"
        >
            <Modal.Header closeButton>
                <Modal.Title>Create new Job Offer</Modal.Title>
            </Modal.Header>
            <Formik
                initialValues={initialValues}
                onSubmit={props.onSave}
                validationSchema={validationSchema}
            >
                {
                    formik => {
                        return (
                            <>
                                <Form onSubmit={formik.handleSubmit} noValidate>
                                    <Modal.Body>
                                        <Row>
                                            <Col className="col-2 d-flex">
                                                <p className="mb-0 ms-3 align-self-center">Customer</p>
                                            </Col>
                                            <Col style={{zIndex: 1000}}>
                                                <Select
                                                    isMulti={false}
                                                    options={props.cOptions}
                                                    onChange={(selected) => {
                                                        formik.setFieldValue("customer", selected)
                                                    }}
                                                    value={formik.values.customer}
                                                    getOptionLabel={(option) => `${option.first_name} ${option.last_name}`}
                                                    getOptionValue={(option) => option.id}
                                                />
                                            </Col>
                                        </Row>
                                        <Row style={{height: "2.5rem"}}>
                                            {
                                                formik.errors.customer && <p className="text-danger text-center fw-bold pt-1">{formik.errors.customer.label}</p>
                                            }
                                        </Row>
                                        <BasicTextInput
                                            name="title"
                                            label="Job Offer Title"
                                            value={formik.values.title}
                                            touched={formik.touched.title}
                                            error={formik.errors.title}
                                            onChange={formik.handleChange}
                                            onBlur={formik.handleBlur}
                                            disabled={formik.isSubmitting}
                                        />
                                        <Spacer height="1rem"/>
                                        <BasicTextArea
                                            name="description"
                                            label="Job Offer Description"
                                            value={formik.values.description}
                                            touched={formik.touched.description}
                                            error={formik.errors.description}
                                            onChange={formik.handleChange}
                                            onBlur={formik.handleBlur}
                                            disabled={formik.isSubmitting}
                                        />
                                        <Spacer height="1rem"/>
                                        <Row>
                                            <Col className="col-3 ms-2">
                                                <p className="mb-0" style={{marginTop: ".35rem"}}>Job Offer skills</p>
                                            </Col>
                                            <Col>
                                                <EditableBadgeList
                                                    badges={formik.values.skills.map((s: Skill) => ({
                                                        key: s,
                                                        text: s
                                                    } as BadgeEntry))}
                                                    label="Skills"
                                                    onRemove={async (entry) => {
                                                        formik.setFieldValue("skills", formik.values.skills.filter((s: Skill) => s !== entry.text))
                                                    }}
                                                    onAdd={async (entry) => {
                                                        formik.setFieldValue("skills", [...formik.values.skills, entry.text as Skill])
                                                    }}
                                                    left
                                                    disabled={formik.isSubmitting}
                                                />
                                            </Col>
                                        </Row>
                                        <Spacer height="1rem"/>
                                        <Row>
                                            <Col className="col-6">
                                                <BasicTextInput
                                                    type="number"
                                                    name="duration"
                                                    label="Job Offer Duration (days)"
                                                    value={formik.values.duration}
                                                    touched={formik.touched.duration}
                                                    error={formik.errors.duration}
                                                    onChange={formik.handleChange}
                                                    onBlur={formik.handleBlur}
                                                    disabled={formik.isSubmitting}
                                                />
                                            </Col>
                                            <Col className="col-6">
                                                <BasicTextInput
                                                    type="number"
                                                    name="budget"
                                                    label="Job Offer Budget"
                                                    value={formik.values.budget}
                                                    touched={formik.touched.budget}
                                                    error={formik.errors.budget}
                                                    onChange={formik.handleChange}
                                                    onBlur={formik.handleBlur}
                                                    disabled={formik.isSubmitting}
                                                />
                                            </Col>
                                        </Row>
                                    </Modal.Body>
                                    <Modal.Footer>
                                        <Button variant="secondary" onClick={props.onHide}>Close</Button>
                                        <Button type="submit" variant="primary">Save changes</Button>
                                    </Modal.Footer>
                                </Form>

                            </>
                        )
                    }
                }
            </Formik>
        </Modal>
    )
}

export default JobOffers;