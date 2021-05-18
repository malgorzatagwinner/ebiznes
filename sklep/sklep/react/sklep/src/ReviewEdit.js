import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { useParams, useHistory } from 'react-router';
import {getRequest} from "./utils";

export default function(){
	const {id} = useParams();
	const [review, setReview] = useState({stars:'', txt:'',user_id:''})
	const history = useHistory();
	
	useEffect(function(){
	async function a(){
		const reviews = await getRequest(`http://localhost:9000/api/review/${id}`);
		setReview(reviews);
	}
	a()
}, [])

	return(
	<Formik

       initialValues={review}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/review/${id}`, values, `PUT`);
	history.push('/review')

       }}

     >

       <Form>

     <label htmlFor="stars">stars</label>
         <Field name="stars" type="number" />
         <ErrorMessage name="stars" />
         <br />
         <label htmlFor="txt">txt</label>
         <Field name="txt" type="text" />
         <ErrorMessage name="txt" />
         <br />
         <label htmlFor="user_id">user_id</label>
         <Field name="user_id" type="number" />
         <ErrorMessage name="user_id" />
         <br />
         <button type="submit">Submit</button>

       </Form>

     </Formik>

	)
}
