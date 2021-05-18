import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';


export default function(){
	const [orders, setPayments] = useState([]);
	const history = useHistory();
	const [order, setPayment] = useState({user_id:'', payment_id:''})
	
	async function del(id){
		await getRequest(`http://localhost:9000/api/order/${id}`, undefined, 'DELETE');
		history.go(0)
	}
	
	useEffect(function(){
		async function a(){
			const orders = await getRequest('http://localhost:9000/api/order');
			setPayments(orders);
		}
		a()
	}, [])

 return (	<div className="orders">
			{orders.map((order, index) =>
			<div key={order.id}>
				<h3><Link to={`/order/${order.id}`}>{order.id}: {order.user_id} {order.payment_id}</Link></h3>
				<h3><a onClick={()=>{del(order.id)}}>delete</a></h3>      
			</div>
 			)}
<Formik

    
       initialValues={order}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/order`, values, `POST`);
	history.go(0)

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
         <br />
         <button type="submit">Submit</button>

       </Form>

     </Formik>
     </div>

   );

 };
