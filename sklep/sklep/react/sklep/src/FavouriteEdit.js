import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { useParams, useHistory } from 'react-router';
import {getRequest} from "./utils";

export default function(){
	const {id} = useParams();
	const [favourite, setFavourite] = useState({film_id:''})
	const history = useHistory();
	
	useEffect(function(){
	async function a(){
		const favourites = await getRequest(`http://localhost:9000/api/favourite/${id}`);
		setFavourite(favourites);
	}
	a()
}, [])

	return(
	<Formik

       initialValues={favourite}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/favourite/${id}`, values, `PUT`);
	history.push('/favourite')

       }}

     >

       <Form>

         <label htmlFor="film_id">film_id</label>
         <Field name="film_id" type="number" />
         <ErrorMessage name="film_id" />
 
         <button type="submit">Submit</button>


       </Form>

     </Formik>

	)
}
