import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';


export default function(){
	const [payments, setPayments] = useState([]);
	const history = useHistory();
	const [payment, setPayment] = useState({user_id:'', amount:''})
	
	async function del(id){
		await getRequest(`http://localhost:9000/api/payment/${id}`, undefined, 'DELETE');
		history.go(0)
	}
	
	useEffect(function(){
		async function a(){
			const payments = await getRequest('http://localhost:9000/api/payment');
			setPayments(payments);
		}
		a()
	}, [])

 return (	<div className="payments">
			{payments.map((payment, index) =>
			<div key={payment.id}>
				<h3><Link to={`/payment/${payment.id}`}>{payment.id}: {payment.user_id} {payment.amount}</Link></h3>
				<h3><a onClick={()=>{del(payment.id)}}>delete</a></h3>      
			</div>
 			)}
<Formik

    
       initialValues={payment}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/payment`, values, `POST`);
	history.go(0)

       }}
     >

       <Form>

         <label htmlFor="user_id">user_id</label>
         <Field name="user_id" type="number" />
         <ErrorMessage name="user_id" />
         <br />
         <label htmlFor="amount">amount</label>
         <Field name="amount" type="number" />
         <ErrorMessage name="amount" />
         <br />
         <button type="submit">Submit</button>

       </Form>

     </Formik>
     </div>

   );

 };
