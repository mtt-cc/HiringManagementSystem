const URL = "http://localhost:8080"

export const Configuration = {
    url: URL,
    routes: {
        me: URL + "/user/me",
        document_store: {
            get: {
                documents: (
                    page: number,
                    size: number,
                ) => {
                    const url = URL + "/API/v1/document_store/documents?page=" + page + "&size=" + size;
                    return url;
                },
                document: (id: number) => URL + "/API/v1/document_store/documents/" + id,
                documentData: (id: number) => URL + "/API/v1/document_store/documents/" + id + "/data",
            },
            post: {
                document: (id: number) => URL + "/API/v1/document_store/documents/" + id
            },
            put: {
                documentName: (id: number) => URL + "/API/v1/document_store/documents/" + id + "/name",
            },
            delete: {
                // NOT NEEDED
            }
        }
    }
}