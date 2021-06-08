import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { useParams, useHistory } from 'react-router';
import {getRequest} from "./utils";

export default function(){
	const {id} = useParams();
	const [director, setDirector] = useState({name:''})
	const history = useHistory();
	
	useEffect(function(){
	async function a(){
		const directors = await getRequest(`http://localhost:9000/api/director/${id}`);
		setDirector(directors);
	}
	a()
}, [])

	return(
	<Formik

       initialValues={director}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/director/${id}`, values, `PUT`);
	history.push('/director')

       }}

     >

       <Form>

         <label htmlFor="name">Name</label>
         <Field name="name" type="text" />
         <ErrorMessage name="name" />
         <br />
         
         <button type="submit">Submit</button>
       </Form>

     </Formik>

	)
}
