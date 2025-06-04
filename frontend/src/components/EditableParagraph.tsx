import {useState} from 'react';
import {Button, Card, Container, Form} from "react-bootstrap";
import {CiEdit, CiSaveUp2} from "react-icons/ci";
import {Formik} from "formik";
import BasicTextArea from "./form/BasicTextArea.tsx";
import Spacer from "./Spacer.tsx";

interface EditableParagraphProps {
    title: string;
    body: string;
    bodyLabel?: string;
    validationSchema: any;
    disabled?: boolean;
    onChange: (body: string) => Promise<void>;
}

function EditableParagraph(props: EditableParagraphProps) {
    const [editing, setEditing] = useState(false);

    if (editing && !props.disabled) {
        return (
            <Formik
                initialValues={{
                    body: props.body
                }}
                onSubmit={(values) => {
                    props.onChange(values.body).then(() => {
                        setEditing(false);
                    });
                }}
                validationSchema={props.validationSchema}
            >
                {
                    (formik) => (
                        <Form onSubmit={formik.handleSubmit} noValidate>
                            <Card>
                                <Card.Body>
                                    {
                                        props.title.length > 0 ?
                                            <Card.Title>
                                                {props.title}
                                            </Card.Title>
                                            :
                                            null
                                    }
                                    <BasicTextArea
                                        name={"body"}
                                        label={props.bodyLabel ? props.bodyLabel : "Body"}
                                        value={formik.values.body}
                                        touched={formik.touched.body}
                                        error={formik.errors.body}
                                        onChange={formik.handleChange}
                                        onBlur={formik.handleBlur}
                                        disabled={formik.isSubmitting}
                                    />
                                    <Spacer height={"1rem"} borderBottom={".1rem solid lightgray"}/>
                                    <Container fluid className="d-flex justify-content-end">
                                        <Button type="submit" disabled={formik.isSubmitting}>
                                            <span className="d-flex align-content-center">Save <CiSaveUp2 className="ms-2" size="1.5rem"/></span>
                                        </Button>
                                    </Container>
                                </Card.Body>
                            </Card>
                        </Form>
                    )
                }
            </Formik>
        )
    } else {
        return (
            <Card>
                <Card.Body>
                    {
                        props.title.length > 0 ?
                            <Card.Title>
                                {props.title}
                            </Card.Title>
                            :
                            null
                    }
                    <Card.Text>
                        {props.body}
                    </Card.Text>
                    <Container fluid className="d-flex justify-content-end">
                        {
                            !props.disabled &&
                            <Button onClick={() => setEditing(true)}>
                                <span className="d-flex align-content-center">Edit <CiEdit className="ms-2" size="1.5rem"/></span>
                            </Button>
                        }
                    </Container>
                </Card.Body>
            </Card>
        )
    }
}

export default EditableParagraph;