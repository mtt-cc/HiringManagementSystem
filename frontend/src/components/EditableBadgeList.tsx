import {useEffect, useState} from 'react';
import {Badge, Button, Form, Modal, Stack} from "react-bootstrap";
import {CiCircleCheck, CiCircleRemove, CiSquareCheck, CiSquareRemove} from "react-icons/ci";
import {BadgeEntry} from "./BadgeList.tsx";
import {PiPlus} from "react-icons/pi";
import {Formik} from "formik";
import * as Yup from "yup";
import BasicTextInput from "./form/BasicTextInput.tsx";

interface EditableBadgeListProps {
    badges: BadgeEntry[];
    bg?: string;
    pill?: boolean;
    label: string;
    left?: boolean;
    onRemove: (badge: BadgeEntry) => Promise<void>;
    onAdd: (badge: BadgeEntry) => Promise<void>;
    disabled?: boolean;
}

interface RemovableBadgeProps {
    text: string;
    bg?: string;
    pill?: boolean;
    onRemove: () => Promise<void>;
    disabled?: boolean;
}

function EditableBadgeList(props: EditableBadgeListProps) {
    const [showAddModal, setShowAddModal] = useState<boolean>(false);
    return (
        <>
            <Stack direction="horizontal" gap={2}
                   className={`d-flex flex-wrap ${props.left ? "justify-content-start" : "justify-content-center"}`}>
                {
                    props.badges.map((badge, index) => {
                        return (
                            <RemovableBadge
                                key={index}
                                text={badge.text}
                                bg={props.bg}
                                pill={props.pill}
                                onRemove={() => props.onRemove(badge)}
                                disabled={props.disabled}
                            />
                        );
                    })
                }
                <Button
                    variant={`outline-${props.bg ? props.bg : "primary"}`}
                    style={{padding: ".25rem", paddingTop: ".15rem", borderRadius: props.pill ? "50%" : undefined}}
                    onClick={() => setShowAddModal(true)}
                    disabled={props.disabled}
                >
                    <PiPlus size="1.5rem"/>
                </Button>
            </Stack>
            {
                showAddModal ?
                    <AddNewBadgeModal
                        confirm={(badge) => {
                            props.onAdd(badge).then(() => {
                                setShowAddModal(false);
                            });
                        }}
                        cancel={() => setShowAddModal(false)}
                        show={showAddModal}
                        label={props.label}
                    />
                    : null
            }
        </>
    );
}

export function RemovableBadge(props: RemovableBadgeProps) {
    return (
        <Badge
            bg={props.bg ? props.bg : "primary"}
            pill={props.pill}
            style={{paddingLeft: "1rem", paddingTop: "0.15rem", paddingBottom: "0.15rem"}}
            className="d-flex align-items-center"
        >
            <span>{props.text}</span>
            <RemoveIcon onRemove={props.onRemove} pill={props.pill} disabled={props.disabled}/>
        </Badge>
    );
}

function RemoveIcon(props: {
    onRemove: () => Promise<void>,
    pill?: boolean,
    disabled?: boolean
}) {
    const [removeConfirm, setRemoveConfirm] = useState<boolean>(false);
    const [disabled, setDisabled] = useState<boolean>(props.disabled === undefined ? false : props.disabled);

    useEffect(() => {
        if (removeConfirm) {
            setTimeout(() => {
                setRemoveConfirm(false);
            }, 2000);
        }
    }, [removeConfirm]);

    if (props.pill) {
        if (disabled) {
            return (
                <CiCircleCheck
                    size={"2rem"}
                    className="ms-3"
                    style={{opacity: .7}}
                />
            )
        } else {
            if (removeConfirm) {
                return (
                    <CiCircleCheck
                        size={"2rem"}
                        onClick={() => {
                            setDisabled(true);
                            props.onRemove().then(() => {
                                setDisabled(false);
                            });
                        }}
                        className="ms-3"
                        style={{cursor: "pointer"}}
                    />
                )
            } else {
                return (
                    <CiCircleRemove
                        size={"2rem"}
                        onClick={() => setRemoveConfirm(true)}
                        className="ms-3"
                        style={{cursor: "pointer"}}
                    />
                )
            }
        }
    } else {
        if (disabled) {
            return (
                <CiSquareCheck
                    size={"2rem"}
                    className="ms-3"
                    style={{opacity: .7}}
                />
            )
        } else {
            if (removeConfirm) {
                return (
                    <CiSquareCheck
                        size={"2rem"}
                        onClick={() => {
                            setDisabled(true);
                            props.onRemove().then(() => {
                                setDisabled(false);
                            });
                        }}
                        className="ms-3"
                        style={{cursor: "pointer"}}
                    />
                )
            } else {
                return (
                    <CiSquareRemove
                        size={"2rem"}
                        onClick={() => setRemoveConfirm(true)}
                        className="ms-3"
                        style={{cursor: "pointer"}}
                    />
                )
            }
        }
    }
}

function AddNewBadgeModal(props: {
    confirm: (badge: BadgeEntry) => void,
    cancel: () => void,
    show: boolean,
    label: string,
}) {
    return (
        <Formik
            initialValues={{text: ""}}
            onSubmit={(values) => {
                props.confirm({key: "-1", text: values.text});
            }}
            validationSchema={Yup.object({
                text: Yup.string().required("This field is required!")
            })}
        >
            {
                (formik) => (
                    <Modal onHide={props.cancel} show={props.show}>
                        <Form onSubmit={formik.handleSubmit} noValidate>
                            <Modal.Header>
                                <Modal.Title>Add new {props.label.toLowerCase()}</Modal.Title>
                            </Modal.Header>
                            <Modal.Body>
                                <BasicTextInput
                                    name={"text"}
                                    label={props.label}
                                    value={formik.values.text}
                                    touched={formik.touched.text}
                                    error={formik.errors.text}
                                    onChange={formik.handleChange}
                                    onBlur={formik.handleBlur}
                                    type={"text"}
                                    disabled={formik.isSubmitting}
                                />
                            </Modal.Body>
                            <Modal.Footer>
                                <Button variant="secondary" disabled={formik.isSubmitting} onClick={() => {
                                    formik.resetForm();
                                    props.cancel();
                                }}>Cancel</Button>
                                <Button variant="primary" type="submit" disabled={formik.isSubmitting}>Add</Button>
                            </Modal.Footer>
                        </Form>
                    </Modal>
                )
            }
        </Formik>

    );
}

export default EditableBadgeList;