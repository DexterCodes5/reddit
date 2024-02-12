export class UserModel {
    id: number
    username: string
    email: string
    img: string
    accessToken: string
    refreshToken: string
    
    constructor(id: number, username: string, email: string, img: string, accessToken: string, refreshToken: string) {
        this.id = id
        this.username = username
        this.email = email
        this.img = img
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }
}