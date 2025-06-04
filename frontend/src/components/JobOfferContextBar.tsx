import React, {useState} from "react";
import {IconType} from "react-icons";
import {Button, Modal, OverlayTrigger, Tooltip, TooltipProps, Spinner} from "react-bootstrap";
import {JobOfferStatus} from "../types/JobOffer.ts";
import {CiNoWaitingSign, CiPlay1, CiRedo, CiStickyNote} from "react-icons/ci";
import {JSX} from "react/jsx-runtime";

function JobOfferContextButton(props: {
    icon: React.ReactElement<IconType>,
    tooltip: string,
    variant: string,
    onClick: () => void,
    disabled?: boolean,
}) {
    const tooltip = (p: JSX.IntrinsicAttributes & TooltipProps & React.RefAttributes<HTMLDivElement>) => {
        return (
            <Tooltip id={`tooltip-4-${props.icon}`} {...p}>
                {props.tooltip}
            </Tooltip>
        )
    }

    return (
        <OverlayTrigger
            placement="top"
            overlay={tooltip}
        >
            <Button variant={`outline-${props.variant}`} className="me-2" onClick={props.onClick} disabled={props.disabled}>
                {
                    props.disabled ?
                        <Spinner animation="border" size="sm" role="status" aria-hidden="true"/>
                        :
                        props.icon
                }
            </Button>
        </OverlayTrigger>
    )
}

function JobOfferContextBar(props: {
    status: JobOfferStatus,
    onStateChangeRequest: (newState: JobOfferStatus) => Promise<void>,
    showNotes: () => void,
}) {
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [modalInfo, setModalInfo] = useState<ConfirmationModalInfo | null>(null);
    const [disabled, setDisabled] = useState<number | null>(null);

    return (
        <>
            {/* Notes - Always visible, if in aborted it will be readonly */}
            <JobOfferContextButton
                icon={<CiStickyNote size={"1.5rem"}/>}
                tooltip="Open job offer notes"
                variant="secondary"
                onClick={props.showNotes}
            />

            {/* Back to Selection Phase - Only visible if in candidate proposal, consolidated or done */}
            {(props.status === JobOfferStatus.CANDIDATE_PROPOSAL || props.status === JobOfferStatus.CONSOLIDATED || props.status === JobOfferStatus.DONE) &&
                <JobOfferContextButton
                    icon={<CiRedo size={"1.5rem"}/>}
                    tooltip="Back to selection phase"
                    variant="primary"
                    onClick={() => {
                        setShowConfirmation(true);
                        setModalInfo({
                            title: "Move back to selection phase",
                            body: "Are you sure you want to go back to the selection phase? All progresses will be lost, you will maintain only your notes.",
                            onConfirm: () => {
                                setDisabled(1);
                                props.onStateChangeRequest(JobOfferStatus.SELECTION_PHASE).then(() => {
                                    setDisabled(null)
                                })
                                setShowConfirmation(false);
                            }
                        });
                    }}
                    disabled={disabled === 1}
                />
            }

            {/* CREATED -> SELECTION_PHASE */}
            {props.status === JobOfferStatus.CREATED &&
                <JobOfferContextButton
                    icon={<CiPlay1 size={"1.5rem"}/>}
                    tooltip="Go to candidate selection"
                    variant="success"
                    onClick={() => {
                        setShowConfirmation(true);
                        setModalInfo({
                            title: "Proceed to selection phase",
                            body: "The job offer will be considered as consolidated, you will still be able to edit job offer skills and notes",
                            onConfirm: () => {
                                setDisabled(2)
                                props.onStateChangeRequest(JobOfferStatus.SELECTION_PHASE).then(() => {
                                    setDisabled(null)
                                })
                                setShowConfirmation(false);
                            }
                        });
                    }}
                    disabled={disabled === 2}
                />
            }

            {/* SELECTION_PHASE -> CANDIDATE_PROPOSAL */}
            {props.status === JobOfferStatus.SELECTION_PHASE &&
                <JobOfferContextButton
                    icon={<CiPlay1 size={"1.5rem"}/>}
                    tooltip="Proceed to candidate proposal"
                    variant="success"
                    onClick={() => {
                        setShowConfirmation(true);
                        setModalInfo({
                            title: "Proceed to candidate proposal",
                            body: "The customer will receive the selected candidates, the job offer information will become unmodifiable. Make sure to have selected one candidate from the list.",
                            onConfirm: () => {
                                setDisabled(3)
                                props.onStateChangeRequest(JobOfferStatus.CANDIDATE_PROPOSAL).then(() => {
                                    setDisabled(null)
                                });
                                setShowConfirmation(false);
                            }
                        });
                    }}
                    disabled={disabled === 3}
                />
            }

            {/* CANDIDATE_PROPOSAL -> CONSOLIDATED */}
            {props.status === JobOfferStatus.CANDIDATE_PROPOSAL &&
                <JobOfferContextButton
                    icon={<CiPlay1 size={"1.5rem"}/>}
                    tooltip="Consolidate candidate for job offer"
                    variant="success"
                    onClick={() => {
                        setShowConfirmation(true);
                        setModalInfo({
                            title: "Consolidate candidate",
                            body: "The customer has accepted the candidate, the professional is able to start working.",
                            onConfirm: () => {
                                setDisabled(4)
                                props.onStateChangeRequest(JobOfferStatus.CONSOLIDATED).then(() => {
                                    setDisabled(null)
                                });
                                setShowConfirmation(false);
                            }
                        });
                    }}
                    disabled={disabled === 4}
                />
            }

            {/* CONSOLIDATED -> DONE */}
            {props.status === JobOfferStatus.CONSOLIDATED &&
                <JobOfferContextButton
                    icon={<CiPlay1 size={"1.5rem"}/>}
                    tooltip="Finish job offer"
                    variant="success"
                    onClick={() => {
                        setShowConfirmation(true);
                        setModalInfo({
                            title: "Complete job offer",
                            body: "The professional has completed its work, the job offer is completed.",
                            onConfirm: () => {
                                setDisabled(5)
                                props.onStateChangeRequest(JobOfferStatus.DONE).then(() => {
                                    setDisabled(null)
                                });
                                setShowConfirmation(false);
                            }
                        });
                    }}
                    disabled={disabled === 5}
                />
            }

            {/* X -> ABORTED visible in any state different from aborted */}
            {props.status != JobOfferStatus.ABORTED && props.status != JobOfferStatus.DONE &&
                <JobOfferContextButton
                    icon={<CiNoWaitingSign size={"1.5rem"}/>}
                    tooltip="Abort job offer"
                    variant="danger"
                    onClick={() => {
                        setShowConfirmation(true);
                        setModalInfo({
                            title: "Abort job offer",
                            body: "Are you sure you want to abort the job offer? This action cannot be undone.",
                            onConfirm: () => {
                                setDisabled(6)
                                props.onStateChangeRequest(JobOfferStatus.ABORTED).then(() => {
                                    setDisabled(null)
                                });
                                setShowConfirmation(false);
                            }
                        });
                    }}
                    disabled={disabled === 6}
                />
            }

            {/* Confirmation modal */}
            {
                showConfirmation &&
                <ConfirmationModal
                    show={showConfirmation}
                    modalInfo={modalInfo}
                    onHide={() => setShowConfirmation(false)}
                />
            }
        </>
    )
}

type ConfirmationModalInfo = {
    title: string,
    body: string,
    onConfirm: () => void,
}

function ConfirmationModal(props: {
    show: boolean,
    modalInfo: ConfirmationModalInfo | null,
    onHide: () => void
}) {
    if (!props.modalInfo) return null;

    return (
        <Modal onHide={props.onHide} show={props.show}>
            <Modal.Header>
                <Modal.Title className="ms-2">{props.modalInfo.title}</Modal.Title>
            </Modal.Header>
            <Modal.Body>{props.modalInfo.body}</Modal.Body>
            <Modal.Footer>
                <Button onClick={props.onHide} variant="danger">Cancel</Button>
                <Button onClick={props.modalInfo.onConfirm} variant="success">Confirm</Button>
            </Modal.Footer>
        </Modal>
    )
}

export default JobOfferContextBar;