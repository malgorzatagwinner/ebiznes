import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { useParams, useHistory } from 'react-router';
import {getRequest} from "./utils";

export default function(){
	const {id} = useParams();
	const [payment, setPayment] = useState({user_id:'', amount:''})
	const history = useHistory();
	
	useEffect(function(){
	async function a(){
		const payments = await getRequest(`http://localhost:9000/api/payment/${id}`);
		setPayment(payments);
	}
	a()
}, [])

	return(
	<Formik

       initialValues={payment}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/payment/${id}`, values, `PUT`);
	history.push('/payment')

       }}

     >

        <Form>

         <label htmlFor="user_id">user_id</label>
         <Field name="user_id" type="int" />
         <ErrorMessage name="user_id" />
         <br />
         <label htmlFor="amount">amount</label>
         <Field name="amount" type="int" />
         <ErrorMessage name="amount" />
         <br />
         <button type="submit">Submit</button>

       </Form>


     </Formik>

	)
}
