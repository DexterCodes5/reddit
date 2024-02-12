import "./Pagination.css"

export const Pagination: React.FC<{
    currentPage: number, totalPages: number, paginate: Function
}> = (props) => {
    const pageNumbers = []
    if (props.currentPage === 1) {
        pageNumbers.push(props.currentPage)
        if (props.totalPages >= props.currentPage + 1) {
            pageNumbers.push(props.currentPage + 1)
        }
        if (props.totalPages >= props.currentPage + 2) {
            pageNumbers.push(props.currentPage + 2)
        }
    } else if (props.currentPage > 1) {
        if (props.currentPage >= 3) {
            pageNumbers.push(props.currentPage - 2)
            pageNumbers.push(props.currentPage - 1)
        } else {
            pageNumbers.push(props.currentPage - 1)
        }
        pageNumbers.push(props.currentPage)
        if (props.totalPages >= props.currentPage + 1) {
            pageNumbers.push(props.currentPage + 1)
        }
        if (props.totalPages >= props.currentPage + 2) {
            pageNumbers.push(props.currentPage + 2)
        }
    }

    return (
        <nav className="pagination">
            <ul className="pagination-ul">
                {props.currentPage > 1 &&
                    <li className="page-item" onClick={() => props.paginate(1)}>
                        &#9666;
                    </li>
                }
                {pageNumbers.map(pageNum => {
                    if (pageNum === props.currentPage) {
                        return (
                            <li className={`page-item page-item-active`} key={pageNum}>
                                {pageNum}
                            </li>
                        )
                    }
                    return (
                        <li className={`page-item`}
                            onClick={() => props.paginate(pageNum)} key={pageNum}>
                            {pageNum}
                        </li>
                    )
                })}
                {props.currentPage < props.totalPages &&
                    <li className="page-item" onClick={() => props.paginate(props.totalPages)}>
                        &#9656;
                    </li>
                }
            </ul>
        </nav>
    )
}