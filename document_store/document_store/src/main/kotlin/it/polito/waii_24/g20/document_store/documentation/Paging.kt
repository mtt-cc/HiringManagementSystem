package it.polito.waii_24.g20.document_store.documentation

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import it.polito.waii_24.g20.document_store.dtos.DocumentMetadataDTO
import org.hibernate.validator.constraints.Range
import org.springframework.data.domain.PageImpl

abstract class PageParameters {
    @Parameter(description = "The page number", example = "0") @Range(min = 0, message = "Page must be positive")
    annotation class PageNumberParam

    @Parameter(description = "The size of the page", example = "10") @Range(min = 1, message = "Size must be greater than 0")
    annotation class PageSizeParam
}

@Schema(
    name = "PageContactDTO",
    description = "Page of contacts",
    examples = [pageDocumentDTOExample]
)
class PageDocumentDTO : PageImpl<DocumentMetadataDTO>(mutableListOf<DocumentMetadataDTO>())

const val documentMetadataExample = "{'id': 1, 'name': 'document', 'size': 100, 'contentType': 'application/pdf', 'creationTimeStamp': '2021-09-01T00:00:00'}"
const val pageDocumentDTOExample = "{'content': [" +
        documentMetadataExample +
        "], 'pageable': {'sort': {'sorted': false, 'unsorted': true, 'empty': true}, 'offset': 0, 'pageNumber': 0, 'pageSize': 10, 'paged': true, 'unpaged': false}, 'totalPages': 0, 'totalElements': 0, 'last': true, 'size': 10, 'number': 0, 'sort': {'sorted': false, 'unsorted': true, 'empty': true}, 'numberOfElements': 0, 'first': true, 'empty': true}"
