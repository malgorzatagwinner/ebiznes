import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';


export default function(){
	const [shoppingBags, setShoppingBags] = useState([]);
	const history = useHistory();
	const [shoppingBag, setShoppingBag] = useState({ total_cost:'', film_id:''})
	
	async function del(id){
		await getRequest(`http://localhost:9000/api/shoppingBag/${id}`, undefined, 'DELETE');
		history.go(0)
	}
	
	useEffect(function(){
		async function a(){
			const shoppingBags = await getRequest('http://localhost:9000/api/shoppingBag');
			setShoppingBags(shoppingBags);
		}
		a()
	}, [])

 return (	<div className="shoppingBags">
			{shoppingBags.map((shoppingBag, index) =>
			<div key={shoppingBag.id}>
				<h3><Link to={`/shoppingBag/${shoppingBag.id}`}>{shoppingBag.id}: {shoppingBag.total_cost} {shoppingBag.film_id}</Link></h3>
				<h3><a onClick={()=>{del(shoppingBag.id)}}>delete</a></h3>      
			</div>
 			)}
<Formik

    
       initialValues={shoppingBag}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/shoppingBag`, values, `POST`);
	history.go(0)

       }}
     >

       <Form>

         <label htmlFor="total_cost">total_cost</label>
         <Field name="total_cost" type="number" />
         <ErrorMessage name="total_cost" />
         <br />
         <label htmlFor="film_id">film_id</label>
         <Field name="film_id" type="number" />
         <ErrorMessage name="film_id" />
         <br />
         <button type="submit">Submit</button>

       </Form>

     </Formik>
     </div>

   );

 };
