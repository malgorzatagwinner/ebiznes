import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { useParams, useHistory } from 'react-router';
import {getRequest} from "./utils";

export default function(){
	const {id} = useParams();
	const [shoppingBag, setShoppingBag] = useState({total_cost:'', film_id:''})
	const history = useHistory();
	
	useEffect(function(){
	async function a(){
		const shoppingBags = await getRequest(`http://localhost:9000/api/shoppingBag/${id}`);
		setShoppingBag(shoppingBags);
	}
	a()
}, [])

	return(
	<Formik

       initialValues={shoppingBag}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/shoppingBag/${id}`, values, `PUT`);
	history.push('/shoppingBag')

       }}

     >

       <Form>

    
 	 <label htmlFor="total_cost">user_id</label>
         <Field name="total_cost" type="number" />
         <ErrorMessage name="total_cost" />
         <br />
         <label htmlFor="film_id">film_id</label>
         <Field name="film_id" type="number" />
         <ErrorMessage name="film_id" />
 
         <button type="submit">Submit</button>


       </Form>

     </Formik>

	)
}
