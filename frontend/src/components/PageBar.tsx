import {Pagination} from "react-bootstrap";

function PageBar(props: {
    pageNumber: number,
    totalPages: number,
    setPage: (page: number) => void
}) {
    const {pageNumber, totalPages, setPage} = props;
    if (totalPages <= 1) {
        return null;
    } else if (totalPages <= 11) {
        return (
            <Pagination>
                <Pagination.First onClick={() => setPage(0)}/>
                <Pagination.Prev
                    onClick={() => setPage(pageNumber - 1)}
                    disabled={pageNumber === 0}
                />
                {
                    Array.from(Array(totalPages).keys()).map((i) => {
                            return (
                                <Pagination.Item
                                    key={i}
                                    active={i === pageNumber}
                                    onClick={() => setPage(i)}
                                >
                                    {i + 1}
                                </Pagination.Item>
                            )
                        }
                    )
                }
                <Pagination.Next
                    onClick={() => setPage(pageNumber + 1)}
                    disabled={pageNumber === totalPages - 1}
                />
                <Pagination.Last onClick={() => setPage(totalPages - 1)}/>
            </Pagination>
        );
    } else {
        return (
            <Pagination>
                <Pagination.First onClick={() => setPage(0)}/>
                <Pagination.Prev
                    onClick={() => setPage(pageNumber - 1)}
                    disabled={pageNumber === 0}
                />
                <Pagination.Item
                    key={0}
                    active={0 === pageNumber}
                    onClick={() => setPage(0)}
                >
                    {1}
                </Pagination.Item>
                {
                    pageNumber < 6 ?
                        <>
                            {[1, 2, 3, 4, 5, 6].map((i) => {

                                    return (
                                        <Pagination.Item
                                            key={i}
                                            active={i === pageNumber}
                                            onClick={() => setPage(i)}
                                        >
                                            {i + 1}
                                        </Pagination.Item>
                                    )
                                }
                            )}
                            <Pagination.Ellipsis disabled/>
                        </>
                        : pageNumber >= totalPages - 7 ?
                            <>
                                <Pagination.Ellipsis disabled/>
                                {
                                    [totalPages - 7, totalPages - 6, totalPages - 5, totalPages - 4, totalPages - 3, totalPages - 2].map((i) => {

                                            return (
                                                <Pagination.Item
                                                    key={i}
                                                    active={i === pageNumber}
                                                    onClick={() => setPage(i)}
                                                >
                                                    {i + 1}
                                                </Pagination.Item>
                                            )
                                        }
                                    )
                                }

                            </>
                            : <>
                                <Pagination.Ellipsis disabled/>
                                {
                                    [pageNumber - 2, pageNumber - 1, pageNumber, pageNumber + 1, pageNumber + 2].map((i) => {

                                            return (
                                                <Pagination.Item
                                                    key={i}
                                                    active={i === pageNumber}
                                                    onClick={() => setPage(i)}
                                                >
                                                    {i + 1}
                                                </Pagination.Item>
                                            )
                                        }
                                    )
                                }
                                <Pagination.Ellipsis disabled/>
                            </>
                }
                <Pagination.Item
                    key={totalPages - 1}
                    active={totalPages - 1 === pageNumber}
                    onClick={() => setPage(totalPages - 1)}
                >
                    {totalPages}
                </Pagination.Item>
                <Pagination.Next
                    onClick={() => setPage(pageNumber + 1)}
                    disabled={pageNumber === totalPages - 1}
                />
                <Pagination.Last onClick={() => setPage(totalPages - 1)}/>
            </Pagination>
        );
    }
}

export default PageBar;