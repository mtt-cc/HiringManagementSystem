import {Configuration} from "./configuration_document_store.ts";
import {ExpectedBody, fetchDataAsync, Method, uploadFileAsync} from "./utils.ts";
import {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";
import {Pageable} from "../types/Pageable.ts";
import {DocumentMetadataType} from "../types/Document.ts";


// ATTACHMENTS ---------------------------------------------------
export function getAttachments(
    page: number,
    size: number,
    ok: (data: Pageable<DocumentMetadataType>) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.document_store.get.documents(page, size),
    ).then(ok).catch(err).finally(fin);
}   

export async function getAttachment(
    id: number) {
    return fetchDataAsync(
        Configuration.routes.document_store.get.document(id),
    )
}

export async function getAttachmentData(
    id: number) {
    return fetchDataAsync(
        Configuration.routes.document_store.get.documentData(id),
    )
}

export function updateName(
    id: number,
    name: string,
    xsrfToken: string | null,
    ok: (data: object) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {}
) {
    fetchDataAsync(
        Configuration.routes.document_store.put.documentName(id),
        xsrfToken,
        Method.PUT,
        ExpectedBody.TEXT,
        name
    ).then(ok).catch(err).finally(fin);
}