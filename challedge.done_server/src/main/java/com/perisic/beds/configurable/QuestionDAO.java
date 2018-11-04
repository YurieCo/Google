package com.perisic.beds.configurable;

import java.util.List;
import java.util.Vector;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.perisic.beds.rmiinterface.Question;

import utils.HibernateUtil;

public class QuestionDAO {
	/**
	 * this method retrives all questions with answers from database
	 * 
	 * @return
	 */
	public Vector<Question> fetchQuestions() {
		Vector<Question> questionsVector = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			//creating criteria and store result in vector of questions;
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Question> query = builder.createQuery(Question.class);
			Root<Question> root = query.from(Question.class);
			query.select(root);
			Query<Question> q = session.createQuery(query);

			List<Question> questions = q.getResultList();
			questionsVector = new Vector<>(questions);

		} catch (HibernateException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		session.close();
		return questionsVector;
	}
	
	
	/**
	 * this method is used to store data in database or update it
	 * @param questions
	 */
	public void feedData(Vector<Question> questions) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction transaction = session.beginTransaction();
			for (int i = 0; i < questions.size(); i++) {
				Question qq = questions.get(i);

				if (qq.getFrequencies().size() > 0) { //push to database only if has some answer 
					session.saveOrUpdate(qq);
				}
				if (i % 20 == 0) { // 20, same as the JDBC batch size connection.properties
					// flush a batch of inserts and release memory:
					session.flush();
					session.clear();
				}
			}
			transaction.commit();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}

	}

}
