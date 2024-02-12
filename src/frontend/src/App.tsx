import { Route, Routes } from "react-router-dom";
import { HomePage } from "./layouts/HomePage/HomePage"
import { QuestionPage } from "./layouts/QuestionPage/QuestionPage";
import { Layout } from "./layouts/Layout/Layout";
import { ErrorPage } from "./layouts/ErrorPage/ErrorPage"
import { ChangePasswordPage } from "./layouts/components/ChangePasswordPage/ChangePasswordPage";
import { UserQuestionsPage } from "./layouts/UserQuestionsPage/UserQuestionsPage";
import { UserSettingsPage } from "./layouts/UserSettingsPage/UserSettingsPage";

function App() {
  // localStorage.clear()
  return (
    <Routes>
      <Route path="/" element={<Layout />} >
        <Route index element={<HomePage />} />
        <Route path="change-password" element={<ChangePasswordPage/>} />
        <Route path="user/questions/:username" element={<UserQuestionsPage />} />
        <Route path="user/settings/:username" element={<UserSettingsPage />} />
        <Route path=":questionId" element={<QuestionPage />} />
        <Route path="*" element={<ErrorPage />} />
      </Route>
    </Routes>
  );
}

export default App;
