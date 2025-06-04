package it.polito.waii_24.g20.document_store.documentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import it.polito.waii_24.g20.document_store.dtos.DocumentMetadataDTO
import org.hibernate.validator.constraints.Range
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile

abstract class DocumentParameters {
    @Parameter(
        description = "The id of the metadata",
        example = "1"
    )
    @Range(
        min = 1,
        message = "Contact id must be positive"
    )
    annotation class MetadataId


    // non worka todo
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "File to be added or updated",
        required = true,
        content = [Content(
            mediaType = "multipart/form-data",
            contentSchema = Schema(
                type = "application/octet-stream",
                format = "binary"
            )
        )]
    )
    annotation class FileBody
}

abstract class CustomerMethods {
    @Operation(
        summary = "List all document metadata",
        description = "List all document metadata in the system. The result is paginated.",
        tags = ["Documents"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The list of documents' metadata",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = PageDocumentDTO::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid query parameters",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    annotation class GetAllDocumentMetadata

    @Operation(
        summary = "Get the document metadata",
        description = "Get the document metadata by id",
        tags = ["Documents"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Document metadata",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = DocumentMetadataDTO::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid query parameters",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Document not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    annotation class GetDocumentMetadata

    @Operation(
        summary = "Get the document",
        description = "Get the document by id",
        tags = ["Documents"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Document as a response entity containing the file",
                content = [Content(
                    mediaType = "various",
                    schema = Schema(implementation = ResponseEntity::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid query parameters",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Document not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    annotation class GetDocument

    @Operation(
        summary = "Add a file",
        description = "Add a file to the system. The payload is a form/multipart containing the file",
        tags = ["Documents"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Document uploaded successfully, it returns the id of the new document",
                content = [Content(
                    mediaType = "text/plain",
                    schema = Schema(
                        implementation = Long::class,
                        example = "1"
                    )
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request, invalid document attached | Invalid document metadata",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict, duplicate document name",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    annotation class AddDocument

    @Operation(
        summary = "Update a file",
        description = "Update a file to the system. The payload is a form/multipart containing the file",
        tags = ["Documents"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Document updated successfully",
                content = [Content(
                    mediaType = "text/plain",
                    schema = Schema(
                        implementation = String::class,
                        example = "Document updated successfully"
                    )
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request, invalid document attached | Invalid document metadata",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Document not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict, duplicate document name",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    annotation class UpdateDocument

    @Operation(
        summary = "Delete a document",
        description = "Delete a document by its id.",
        tags = ["Documents"]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Document deleted successfully",
                content = [Content(
                    mediaType = "text/plain",
                    schema = Schema(
                        implementation = String::class,
                        example = "Document \$id  deleted successfully"
                    )
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid document id",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Document not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    annotation class DeleteDocument
}