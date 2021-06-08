import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { useParams, useHistory } from 'react-router';
import {getRequest} from "./utils";

export default function(){
	const {id} = useParams();
	const [genre, setGenre] = useState({name:''})
	const history = useHistory();
	
	useEffect(function(){
	async function a(){
		const genres = await getRequest(`http://localhost:9000/api/genre/${id}`);
		setGenre(genres);
	}
	a()
}, [])

	return(
	<Formik

       initialValues={genre}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/genre/${id}`, values, `PUT`);
	history.push('/genre')

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
