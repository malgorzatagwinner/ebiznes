import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';
import {useUser} from './UserContext';
import { GoogleLoginButton, GithubLoginButton } from "react-social-login-buttons";

export default () => {
        const [user, setUser] = useState({email:'', password:''})
        const history = useHistory();

        const [,userDispatch] = useUser();
	const [errors, setErrors] = useState();
        return( 
		<>
	<h2>Sign In</h2>
<Formik

    
       initialValues={user}
       enableReinitialize={true}

       onSubmit={async (user) => {
        userDispatch({type: 'signIn', user}).then(()=>{ 
               history.push("/")
        })
        .catch((e) =>{
                setErrors(e.error);
        })

       }}
     >

       <Form>
	{errors && <p key={errors}>{errors} </p>} 
        <label htmlFor="email">email</label>
        <Field name="email" type="text" />
        <ErrorMessage name="email" />
        <br />
        <label htmlFor="password">password</label>
        <Field name="password" type="text" />
        <ErrorMessage name="password" />
        <br />
        <button type="submit"> submit</button>
       </Form>
</Formik>
	<div>OR</div>
        <div id="buttons">
        <GoogleLoginButton onClick={() => window.location.href="https://ebiznes--2021.azurewebsites.net/api/authenticate/google"}/>
        <br />
        <GithubLoginButton onClick={() => window.location.href="https://ebiznes--2021.azurewebsites.net/api/authenticate/github"}/>
	</div>
	</>
	);
}


