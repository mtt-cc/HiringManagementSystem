export type Pageable<T> = {
    content: T[],
    pageable: {
        pageNumber: number,
        pageSize: number,
        sort: {
            sorted: boolean,
            empty: boolean,
            unsorted: boolean
        },
        offset: number,
        paged: boolean,
        unpaged: boolean
    },
    last: boolean,
    totalPages: number,
    totalElements: number,
    first: boolean,
    size: number,
    number: number,
    sort: {
        sorted: boolean,
        empty: boolean,
        unsorted: boolean
    },
    numberOfElements: number,
    empty: boolean
}