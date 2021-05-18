import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { useParams, useHistory } from 'react-router';
import {getRequest} from "./utils";

export default function(){
	const {id} = useParams();
	const [order, setOrder] = useState({user_id:'', payment_id:''})
	const history = useHistory();
	
	useEffect(function(){
	async function a(){
		const orders = await getRequest(`http://localhost:9000/api/order/${id}`);
		setOrder(orders);
	}
	a()
}, [])

	return(
	<Formik

       initialValues={order}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/order/${id}`, values, `PUT`);
	history.push('/order')

       }}

     >

       <Form>

    
 	 <label htmlFor="user_id">user_id</label>
         <Field name="user_id" type="number" />
         <ErrorMessage name="user_id" />
         <br />
         <label htmlFor="payment_id">payment_id</label>
         <Field name="payment_id" type="number" />
         <ErrorMessage name="payment_id" />
 
         <button type="submit">Submit</button>


       </Form>

     </Formik>

	)
}
