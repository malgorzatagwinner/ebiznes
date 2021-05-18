import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { useParams, useHistory } from 'react-router';
import {getRequest} from "./utils";

export default function(){
	const {id} = useParams();
	const [actor, setActor] = useState({name:''})
	const history = useHistory();
	
	useEffect(function(){
	async function a(){
		const actors = await getRequest(`http://localhost:9000/api/actor/${id}`);
		setActor(actors);
	}
	a()
}, [])

	return(
	<Formik

       initialValues={actor}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/actor/${id}`, values, `PUT`);
	history.push('/actor')

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
