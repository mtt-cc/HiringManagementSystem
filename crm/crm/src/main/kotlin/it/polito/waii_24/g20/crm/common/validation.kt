package it.polito.waii_24.g20.crm.common

/**
 * This function checks if an email is valid.
 *
 * @param email[String] the email to check
 *
 * @return [Boolean] true if the email is valid, false otherwise
 */
fun isValidEmail(email: String): Boolean {
    // Implement your email validation logic here
    val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"
    return email.matches(emailRegex.toRegex())
}

/**
 * This function checks if a telephone number is valid.
 *
 * @param telephone[String] the telephone number to check
 *
 * @return [Boolean] true if the telephone number is valid, false otherwise
 */
fun isValidTelephone(telephone: String): Boolean {
    // Implement your telephone number validation logic here based on the provided regex pattern
    // /^\+?[0-9]{1,3}(\s|\-)?\(?\d{1,4}\)?(\s|\-)?\d{1,4}(\s|\-)?\d{1,4}(\s|\-)?\d{1,4}$/          SAME AS FRONTEND
    val telephoneRegex = "^\\+?[0-9]{1,3}(\\s|-)?\\(?\\d{1,4}\\)?(\\s|-)?\\d{1,4}(\\s|-)?\\d{1,4}(\\s|-)?\\d{1,4}$"
    return telephone.matches(telephoneRegex.toRegex())
}