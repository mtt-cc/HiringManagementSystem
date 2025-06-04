

export type DocumentMetadataType = {
    id: number,
    name: string,
    type: string,
    size: number,
    date: string,
    document_content: DocumentContentType
}
    

export type DocumentContentType = {
    id: number,
    document_content: Uint8Array,
}
