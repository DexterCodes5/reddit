import "./QuestionAnswerHeader.css"

type Data = {
    img: string | undefined
    username: string
    postTimestamp: string
}

export const QuestionAnswerHeader: React.FC<{ data: Data }> = (props) => {    
    const miliSeconds = Date.parse(props.data.postTimestamp)
    const timePassed = Date.now() - miliSeconds
    const minutes = Math.round(timePassed / 1000 / 60)
    let time = ""
    if (minutes === 1) {
        time = `${minutes} minute ago`
    } else {
        time = `${minutes} minutes ago`
    }
    if (minutes >= 60) {
        const hours = Math.round(minutes / 60)
        if (hours === 1) {
            time = `${hours} hour ago`
        } else {
            time = `${hours} hours ago`
        }
        if (hours >= 24) {
            const days = Math.round(hours / 24)
            if (days === 1) {
                time = `${days} day ago`
            } else {
                time = `${days} days ago`
            }
        }
    }
    
    return (
        <div className="question-header">
            <img className="question-user-img" src={props.data.img || require("../../../images/user.jpg")} alt="user_icon" />
            <p>{props.data.username}</p>
            <p>{time}</p>
        </div>
    )
}