import React, { useState } from 'react';
import { Formik, Form, FormikState } from 'formik';
import * as Yup from 'yup';
import { Button, Col, Row, Spinner } from 'react-bootstrap';
import BasicTextInput from './form/BasicTextInput';
import BasicSelectInput from './form/BasicSelectInput';
import { Contact, ContactType } from '../types/Contact';
import { IoIosPerson } from 'react-icons/io';
import { BiCategory } from 'react-icons/bi';
import {Channel, channelToString, MessageType} from '../types/Message';

function NewMessageForm(props: {
    initialValues: MessageType,
    handleSubmit: (values: MessageType, { setSubmitting }: any) => void,
    submitLabel: string,
    handleClose: () => void,
    setReload: (reload: boolean) => void
}) {
    const [contacts] = useState<Contact[]>([]);
    const [filteredContacts, setFilteredContacts] = useState<Contact[]>(contacts);
    const [search, setSearch] = useState('');


    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>, setFieldValue: (field: string, value: any) => void) => {
        const searchValue = e.target.value;
        setSearch(searchValue);
        setFilteredContacts(
            contacts.filter(contact =>
                contact.name.toLowerCase().includes(searchValue.toLowerCase())
            )
        );
        setFieldValue('sender', searchValue);
    };

    const handleContactSelect = (contact: Contact, setFieldValue: (field: string, value: any) => void) => {
        setFieldValue('sender', contact.name);
        setSearch('');
        setFilteredContacts(contacts);
    };

    function resetFormWithChannel(setFieldValue, resetForm: (nextState?: Partial<FormikState<MessageType>>) => void, newChannel: Channel, currentValues: MessageType) {
        setFieldValue('sender', '');
        setSearch('');
        setFilteredContacts(contacts);

        resetForm({
            values: {
                ...props.initialValues,
                channel: newChannel,
                subject: currentValues.subject,
                body: currentValues.body
            }
        });
    };

    const validationSchema = Yup.object({
        channel: Yup.string().required('Channel is required'),
        sender: Yup.string().when('channel', {
            is: (val) => val === Channel.PhoneNumber,
            then: () => Yup.string().matches(/^\d{10}$/, 'Invalid phone number').required('Sender is required'),
            otherwise: () => Yup.string().when('channel', {
                is: (val) => val === Channel.Email,
                then: () => Yup.string().email('Invalid email').required('Sender is required'),
                otherwise: () => Yup.string().required('Sender is required')
            })
        }),
        subject: Yup.string().required('Subject is required'),
        body: Yup.string().required('Body is required'),
    });

    return (
        <Formik
            enableReinitialize
            initialValues={props.initialValues}
            validationSchema={validationSchema}
            onSubmit={(values, actions) => {
                props.handleSubmit(values, actions);
                props.handleClose();
                props.setReload(r => !r);
            }}
        >
            {({ setFieldValue, handleChange, handleBlur, values, touched, errors, isSubmitting, resetForm }) => (
                <Form className="card p-3">
                    <Row className="mb-3">
                        <Col>
                            <BasicSelectInput
                                name="channel"
                                label="Channel"
                                value={values.channel}
                                touched={touched.channel}
                                error={errors.channel}
                                onChange={(e: React.ChangeEvent<HTMLSelectElement>) => {
                                    const newChannel = e.target.value;
                                    handleChange(e);
                                    resetFormWithChannel(setFieldValue, resetForm, newChannel, values);
                                }}
                                onBlur={handleBlur}
                                options={[
                                    { value: Channel.PhoneNumber, label: channelToString(Channel.PhoneNumber) },
                                    { value: Channel.Email, label: channelToString(Channel.Email) },
                                ]}
                                leadingIcon={<BiCategory size="1.3rem" />}
                            />
                        </Col>
                    </Row>
                    <Row className="mb-3">
                        <Col>
                            <BasicTextInput
                                name="sender"
                                label="Sender"
                                type="text"
                                placeholder="Enter sender"
                                value={search}
                                touched={touched.sender}
                                error={errors.sender}
                                onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleSearchChange(e, setFieldValue)}
                                onBlur={handleBlur}
                                forceMinHeight
                                leadingIcon={<IoIosPerson size="1.3rem" />}
                            />
                            {search && (
                                <ul className="list-group">
                                    {filteredContacts.map(contact => (
                                        <li
                                            key={contact.id}
                                            className="list-group-item list-group-item-action"
                                            onClick={() => handleContactSelect(contact, setFieldValue)}
                                        >
                                            {contact.name} ({contact.email})
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </Col>
                    </Row>
                    <Row className="mb-3">
                        <Col>
                            <BasicTextInput
                                name="subject"
                                label="Subject"
                                type="text"
                                placeholder="Enter subject"
                                value={values.subject}
                                touched={touched.subject}
                                error={errors.subject}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                forceMinHeight
                            />
                        </Col>
                    </Row>
                    <Row className="mb-3">
                        <Col>
                            <BasicTextInput
                                name="body"
                                label="Body"
                                as="textarea"
                                placeholder="Enter body"
                                value={values.body}
                                touched={touched.body}
                                error={errors.body}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                forceMinHeight
                            />
                        </Col>
                    </Row>
                    <Button type="submit" disabled={isSubmitting} className="w-100 mt-3" style={{ height: '3rem' }}>
                        {isSubmitting ? <Spinner animation="border" size="sm" /> : props.submitLabel}
                    </Button>
                </Form>
            )}
        </Formik>
    );
}

export default NewMessageForm;