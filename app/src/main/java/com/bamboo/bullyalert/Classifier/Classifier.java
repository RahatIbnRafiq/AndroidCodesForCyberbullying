package com.bamboo.bullyalert.Classifier;

/**
 * Created by Rahat Ibn Rafiq on 10/26/2017.
 */

import android.content.Context;
import android.util.Log;
import com.bamboo.bullyalert.Database.NotificationFeedback;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.model.Comment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Classifier {
    private static final int EPOCH = 1000;
    private static final double LEARNING_RATE = 0.0001;
    private static final int BOOTSTRAP_VARIABLE = 5;
    private static Classifier instance = null;
    private static ArrayList<String> negativeWordList;
    private ArrayList<ClassifierTrainingData> trainingDataList;
    private double [] coefficients;

    private Classifier(Context context) throws Exception
    {
        InputStream is = context.getResources().openRawResource(R.raw.negative_words_list);
        Classifier.negativeWordList = new ArrayList<>();
        loadNegativeWords(is);
        this.trainingDataList = new ArrayList<>();
        is = context.getResources().openRawResource(R.raw.training_data_for_android);
        loadTrainingData(is);
        this.coefficients = new double[4];
        this.coefficients[0]= -0.878;
        this.coefficients[1]= 0.00277;
        this.coefficients[2]= 2.0747;
        this.coefficients[3]= 2.0639;
    }


    public double[] getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(double[] coefficients) {
        this.coefficients = coefficients;
    }

    public double predict(double[]featureValues)
    {
        float yhat = 0;
        for(int i=0;i<this.coefficients.length;i++)
        {
            yhat += (this.coefficients[i]*featureValues[i]);
        }
        return 1/(1+Math.exp(-1*yhat));
    }

    public double[] getFeatureValues(ArrayList<Comment>comments)
    {
        double [] featureValues = new double[4];
        double negativeCommentCount  = 0;
        double totalNegativeWord = 0;
        double negativeWordPerNegativeComment = 0;
        for(Comment comment:comments)
        {
            boolean isNegative = false;
            String commentText = comment.getmCommentText();
            commentText = commentText.toLowerCase().trim();
            commentText = commentText.replaceAll("[^a-z]+", " ");
            StringTokenizer st = new StringTokenizer(commentText);
            while (st.hasMoreTokens()) {
                String word = st.nextToken().trim();
                if (Classifier.negativeWordList.contains(word))
                {
                    isNegative = true;
                    totalNegativeWord++;
                }
            }
            if(isNegative)
                negativeCommentCount++;

        }
        double negativeCommentPercentage = (negativeCommentCount)/(comments.size());
        if(negativeCommentCount > 0)
            negativeWordPerNegativeComment = (totalNegativeWord)/(negativeCommentCount);
        featureValues[0] = 1;
        featureValues[1] = negativeCommentCount;
        featureValues[2] = negativeCommentPercentage;
        featureValues[3] = negativeWordPerNegativeComment;
        return featureValues;
    }

    private void loadNegativeWords(InputStream is) throws IOException {

        if (is != null) {
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    if (!Classifier.negativeWordList.contains(line)) {
                        Classifier.negativeWordList.add(line);
                    }
                }
                reader.close();
            }
            finally {
                try{
                    is.close();
                }catch (IOException ex)
                {
                    Log.e(UtilityVariables.tag,"IO Exception in Classifier class loadNegativeWords function."+ex.toString());
                }

            }
        }
    }

    private void loadTrainingData(InputStream is) throws IOException {
        if (is != null) {
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    String []tokens = line.split(",");
                    ClassifierTrainingData tr = new ClassifierTrainingData();
                    tr.featurevalues[0] = 1.0;
                    tr.featurevalues[1] = Double.parseDouble(tokens[0]);
                    tr.featurevalues[2] = Double.parseDouble(tokens[1]);
                    tr.featurevalues[3] = Double.parseDouble(tokens[2]);
                    tr.truePrediction = Double.parseDouble(tokens[3]);
                    this.trainingDataList.add(tr);
                }
                reader.close();
            }
            finally {
                try{
                    is.close();
                }catch (IOException ex)
                {
                    Log.e(UtilityVariables.tag,"IO Exception in Classifier class loadTrainingData function: "+ex.toString());
                }
            }
        }
    }

    public void updateClassifier(ArrayList<NotificationFeedback> feedbacks)
    {
        for (double coefficient : this.coefficients) {
            Log.i(UtilityVariables.tag, " before updating coefficients: " + coefficient);

        }
        try
        {
            ClassifierTrainingData tr = new ClassifierTrainingData();
            for(int i=0; i< feedbacks.size();i++)
            {
                NotificationFeedback feedback = feedbacks.get(i);
                String[]comments = feedback.getmComments().split("--->");
                ArrayList<Comment> commentList;
                commentList = new ArrayList<>();
                for (String comment : comments) {
                    commentList.add(new Comment(comment, "", 0));
                }
                tr.featurevalues = this.getFeatureValues(commentList);
                int f = Integer.parseInt(feedback.mFeedback);
                double p = Double.parseDouble(feedback.getmPredicted());
                tr.truePrediction = (float)f;
                for(int j=0; j< BOOTSTRAP_VARIABLE;j++)
                    this.trainingDataList.add(tr);
            }
            for(int epoch=0;epoch<Classifier.EPOCH;epoch++)
            {
                double sum_error = 0.0;
                for(int j=0;j<this.trainingDataList.size();j++)
                {
                    tr = this.trainingDataList.get(j);
                    double yhat = this.predict(tr.featurevalues);
                    double error = tr.truePrediction - yhat;
                    sum_error += error*error;
                    for(int i=0;i<tr.featurevalues.length;i++)
                    {
                        this.coefficients[i] = this.coefficients[i]+Classifier.LEARNING_RATE*error*tr.featurevalues[i]*yhat*(1.0-yhat);
                    }

                }
            }
        }catch (Exception e)
        {
            Log.e(UtilityVariables.tag,"Exception: "+this.getClass().getName()+" updateClassifier function: "+e.toString());
        }
        for (double coefficient : this.coefficients) {
            Log.i(UtilityVariables.tag, " after updating coefficients: " + coefficient);
        }
    }

    public static Classifier getInstance(Context context) throws Exception
    {
        if(instance == null) {
            instance = new Classifier(context);
        }
        return instance;
    }
}