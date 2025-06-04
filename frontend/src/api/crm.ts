import {Configuration} from "./configuration_crm.ts";
import {ExpectedBody, fetchDataAsync, Method} from "./utils.ts";
import {ContactId, ContactType, CustomerType, ProfessionalType} from "../types/Contact.ts";
import {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";
import {Pageable} from "../types/Pageable.ts";
import {MessageType} from "../types/Message.ts";
import {JobOffer, JobOfferHeader, JobOfferStatus} from "../types/JobOffer.ts";
import {bool} from "yup";

// CONTACTS ---------------------------------------------------
export function getContacts(
    page: number,
    size: number,
    category: string,
    country: string,
    city: string,
    first_name: string,
    last_name: string,
    ssn: string,
    ok: (data: Pageable<ContactType>) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.get.contacts(page, size, category, country, city, first_name, last_name, ssn),
    ).then(ok).catch(err).finally(fin);
}

export function getContact(
    id: string,
    ok: (data: ContactType | CustomerType | ProfessionalType) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.get.contact(id),
    ).then(ok).catch(err).finally(fin);
}

export function postContact(
    contact: any,
    xsrfToken: string | null,
    ok: (data: object) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.post.contact,
        xsrfToken,
        Method.POST,
        ExpectedBody.TEXT,
        contact
    ).then(ok).catch(err).finally(fin);
}

export function updateContact(
    contact: ContactType | CustomerType | ProfessionalType,
    xsrfToken: string | null | undefined,
    ok: (data: object) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.put.contact(`${contact.id}`),
        xsrfToken,
        Method.PUT,
        ExpectedBody.TEXT,
        contact
    ).then(ok).catch(err).finally(fin);
}

export function deleteContact(
    id: string,
    xsrfToken: string | null | undefined,
    ok: (data: object) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.delete.contact(id),
        xsrfToken,
        Method.DELETE,
        ExpectedBody.TEXT
    ).then(ok).catch(err).finally(fin);
}

// MESSAGES ---------------------------------------------------
export function getMessages(
    page: number,
    size: number,
    sorting: string,
    state: string,
    ok: (data: Pageable<MessageType>) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.get.messages(page, size, sorting, state),
    ).then(ok).catch(err).finally(fin);
}

export function getMessage(
    id: string,
    ok: (data: MessageType) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.get.message(id),
    ).then(ok).catch(err).finally(fin);
}

export function postMessage(
    message: MessageType,
    xsrfToken: string | null,
    ok: (data: object) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.post.message,
        xsrfToken,
        Method.POST,
        ExpectedBody.TEXT,
        message
    ).then(ok).catch(err).finally(fin);
}

export function updateMessageState(
    id: number,
    request: string,
    xsrfToken: string | null | undefined,
    ok: (data: object) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.put.messageState(id),
        xsrfToken,
        Method.POST,
        ExpectedBody.TEXT,
        {
                target_state: request,
                comment: ""
        }
    ).then(ok).catch(err).finally(fin);
}

export function updateMessagePriority(
    id: string,
    target_priority: string,
    xsrfToken: string | null | undefined,
    ok: (data: object) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.put.messagePriority(id),
        xsrfToken,
        Method.PUT,
        ExpectedBody.TEXT,
        { target_priority }
    ).then(ok).catch(err).finally(fin);
}

export function getMessageHistory(
    id: string,
    ok: (data: MessageType[]) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.get.messageHistory(id),
    ).then(ok).catch(err).finally(fin);
}

// JOB OFFERS ---------------------------------------------------

export function getJobOffers(
    page: number,
    size: number,
    states: string[],
    search: string,
    customers: number[],
    professionals: number[],
    valueLow: number,
    valueHigh: number,
    ok: (data: Pageable<JobOfferHeader>) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.get.joboffers(page, size, states, search, customers, professionals, valueLow, valueHigh),
    ).then(ok).catch(err).finally(fin);
}

export function getJobOffer(
    id: string,
    ok: (data: any) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.get.joboffer(id),
    ).then(ok).catch(err).finally(fin);
}

export function getProfessionalOption(
    ok: (data: ContactId[]) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    return fetchDataAsync(
        Configuration.routes.crm.get.professionalOptions,
    ).then(ok).catch(err).finally(fin);
}

export function getCustomerOption(
    ok: (data: ContactId[]) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    return fetchDataAsync(
        Configuration.routes.crm.get.customerOptions,
    ).then(ok).catch(err).finally(fin);
}

export function createJobOffer(
    jobOffer: {title: string, description: string, customer_id: number, skills: string[], notes: string, duration: number, budget: number},
    xsrfToken: string | null,
    ok: (data: object) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.post.joboffer,
        xsrfToken,
        Method.POST,
        ExpectedBody.TEXT,
        jobOffer
    ).then(ok).catch(err).finally(fin);
}

export function updateJobOfferSkills(
    id: string,
    skills: string[],
    xsrfToken: string | null | undefined,
    ok: () => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.put.jobofferSkills(id),
        xsrfToken,
        Method.PUT,
        ExpectedBody.NONE,
        skills
    ).then(ok).catch(err).finally(fin);
}
export function updateJobOfferDescription(
    id: string,
    description: string,
    xsrfToken: string | null | undefined,
    ok: () => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.put.jobofferDescription(id),
        xsrfToken,
        Method.PUT,
        ExpectedBody.NONE,
        {description}
    ).then(ok).catch(err).finally(fin);
}

export function updateJobOfferNotes(
    id: string,
    notes: string,
    xsrfToken: string | null | undefined,
    ok: () => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.put.jobofferNotes(id),
        xsrfToken,
        Method.PUT,
        ExpectedBody.NONE,
        {notes}
    ).then(ok).catch(err).finally(fin);
}

export function updateJobOfferState(
    id: string,
    state: string,
    professional_id: number | null,
    xsrfToken: string | null | undefined,
    ok: () => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.put.jobofferState(id),
        xsrfToken,
        Method.PUT,
        ExpectedBody.NONE,
        {
            status: state,
            professional_id: professional_id
        }
    ).then(ok).catch(err).finally(fin);
}

export function getProfessionals(
    page: number,
    size: number,
    skills: string[],
    jobOfferId: number,
    availableOnly: boolean,
    ok: (data: Pageable<ProfessionalType>) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.get.professional(page, size, skills, jobOfferId, availableOnly),
    ).then(ok).catch(err).finally(fin);
}

export function candidateForJobOffer(
    jobOfferId: number,
    professionalId: number,
    xsrfToken: string | null,
    ok: () => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.post.candidateForJO(jobOfferId.toString(), professionalId.toString()),
        xsrfToken,
        Method.POST,
        ExpectedBody.NONE
    ).then(ok).catch(err).finally(fin);
}

export function updateCandidate(
    id: string,
    notes: string,
    verified: boolean,
    xsrfToken: string | null | undefined,
    ok: () => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.crm.put.candidate(id),
        xsrfToken,
        Method.PUT,
        ExpectedBody.NONE,
        {
            notes,
            verified
        }
    ).then(ok).catch(err).finally(fin);
}