export const USERNAME_REGEX = /^(?=[a-zA-Z0-9._]{5,20}$)(?!.*[_.]{2})[^_.].*[^_.]$/
export const PASSWORD_REGEX = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{5,}$/
export const EMAIL_REGEX = /^[\w-\.]+@[a-z]+\.[\w]{2,4}$/