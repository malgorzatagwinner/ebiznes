import {React, useState, useEffect} from "react";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Link} from 'react-router-dom';
import {getRequest} from "./utils";
import {useHistory} from 'react-router';


export default function(){
	const [users, setUsers] = useState([]);
	const history = useHistory();
	const [user, setUser] = useState({email:'', password:'', surname:'', name:'', address:'', zipcode:'', city:'', country:''})
	
	async function del(id){
		await getRequest(`http://localhost:9000/api/user/${id}`, undefined, 'DELETE');
		history.go(0)
	}
	
	useEffect(function(){
		async function a(){
			const users = await getRequest('http://localhost:9000/api/user');
			setUsers(users);
		}
		a()
	}, [])

 return (	<div className="users">
			{users.map((user, index) =>
			<div key={user.id}>
				<h3><Link to={`/user/${user.id}`}>{user.id}: {user.name} {user.surname}</Link></h3>
				<h3><a onClick={()=>{del(user.id)}}>delete</a></h3>      
			</div>
 			)}
<Formik

    
       initialValues={user}
       enableReinitialize={true}

       onSubmit={async (values, { setSubmitting }) => {
	await getRequest(`http://localhost:9000/api/user`, values, `POST`);
	history.go(0)

       }}
     >

       <Form>

         <label htmlFor="email">email</label>
         <Field name="email" type="text" />
         <ErrorMessage name="email" />
         <br />
         <label htmlFor="password">password</label>
         <Field name="password" type="text" />
         <ErrorMessage name="password" />
         <br />
         <label htmlFor="surname">surname</label>
         <Field name="surname" type="text" />
         <ErrorMessage name="surname" />
         <br />
         <label htmlFor="name">Name</label>
         <Field name="name" type="text" />
         <ErrorMessage name="name" />
         <br />   
         <label htmlFor="address">Address</label>
         <Field name="address" type="text" />
         <ErrorMessage name="address" />
         <br />
         <label htmlFor="zipcode">Email Address</label>
         <Field name="zipcode" type="zipcode" />
         <ErrorMessage name="zipcode" />
         <br />   
         <label htmlFor="city">City</label>
         <Field name="city" type="text" />
         <ErrorMessage name="city" />
         <br />    
         <label htmlFor="country">Country</label>
         <Field name="country" type="text" />
         <ErrorMessage name="country" />
         <br />
         <button type="submit">Submit</button>

       </Form>

     </Formik>
     </div>

   );

 };
