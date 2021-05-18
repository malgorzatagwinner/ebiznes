import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { useParams, useHistory } from 'react-router';
import {getRequest} from "./utils";

export default function(){
	const {id} = useParams();
	const [film, setFilm] = useState({name:'', genre_id:'', director_id:'', actor_id:''})
	const history = useHistory();
	
	useEffect(function(){
	async function a(){
		const films = await getRequest(`http://localhost:9000/api/film/${id}`);
		setFilm(films);
	}
	a()
}, [])

	return(
	<Formik

       initialValues={film}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/film/${id}`, values, `PUT`);
	history.push('/film')

       }}

     >

        <Form>

         <label htmlFor="name">name</label>
         <Field name="name" type="text" />
         <ErrorMessage name="name" />
         <br />
         <label htmlFor="genre_id">genre_id</label>
         <Field name="genre_id" type="number" />
         <ErrorMessage name="genre_id" />
         <br />
         <label htmlFor="director_id">director_id</label>
         <Field name="director_id" type="number" />
         <ErrorMessage name="director_id" />
         <br />
         <label htmlFor="actor_id">actor_id</label>
         <Field name="actor_id" type="number" />
         <ErrorMessage name="actor_id" />
         <br />
         <button type="submit">Submit</button>
       </Form>


     </Formik>

	)
}
