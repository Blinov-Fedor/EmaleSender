package su.blinov.emailsender.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import su.blinov.emailsender.model.Template;

@Dao
public interface TemplateDao {
    @Query("SELECT * FROM templates ORDER BY message")
    List<Template> getAllTemplates();

    @Query("SELECT message FROM templates WHERE sex = :sex")
    String getTemplateBySex(String sex);

    @Insert
    void insertTemplate(Template template);

    @Update
    void updateTemplate(Template template);

    @Delete
    void deleteTemplate(Template template);
}